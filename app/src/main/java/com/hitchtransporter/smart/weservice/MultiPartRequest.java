package com.hitchtransporter.smart.weservice;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ebiztrait on 2/6/16.
 */
public class MultiPartRequest extends Request<Object> {

    private HttpEntity mHttpEntity;
    private Response.Listener responseListener;
    private Map<String, String> requestParams;

    public MultiPartRequest(String url, Map<String, String> params, String path, Response.Listener listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, url, errorListener);
        responseListener = listener;
        requestParams = params;
        if (!TextUtils.isEmpty(path)) {
            Log.e("@@@path", path);
            mHttpEntity = buildMultipartEntity(path);
        } else {
            mHttpEntity = buildMultipartEntity();
        }
    }

    public MultiPartRequest(String url, Map<String, String> params, HashMap<String, String> imagePaths, Response.Listener listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, url, errorListener);
        responseListener = listener;
        requestParams = params;
        mHttpEntity = buildMultipartEntity(imagePaths);
    }

    private HttpEntity buildMultipartEntity(String path) {
        File file = new File(path);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        FileBody fileBody = new FileBody(file, ContentType.create(getMimeType(file.getAbsolutePath())), file.getName());
        builder.addPart("image", fileBody);
        Iterator it = requestParams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            try {
                builder.addPart(pair.getKey().toString(), new StringBody(pair.getValue().toString(), Charset.forName("utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            it.remove();
        }
        return builder.build();
    }

    private HttpEntity buildMultipartEntity() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        Iterator it = requestParams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            try {
                builder.addPart(pair.getKey().toString(), new StringBody(pair.getValue().toString(), Charset.forName("utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            it.remove();
        }
        return builder.build();
    }

    private HttpEntity buildMultipartEntity(HashMap<String, String> imagePaths) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        if (imagePaths != null && imagePaths.size() > 0) {
            Iterator itImage = imagePaths.entrySet().iterator();
            while (itImage.hasNext()) {
                Map.Entry pair = (Map.Entry) itImage.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                if (pair.getValue() != null && pair.getValue().toString().length() > 0) {
                    File file = new File(pair.getValue().toString());
                    FileBody fileBody = new FileBody(file, ContentType.create(getMimeType(file.getAbsolutePath())), file.getName());
                    builder.addPart(pair.getKey().toString(), fileBody);
                }
                itImage.remove();
            }
        }

        Iterator it = requestParams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            try {
                builder.addPart(pair.getKey().toString(), new StringBody(pair.getValue().toString(), Charset.forName("utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            it.remove();
        }
        return builder.build();
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Log.v("@@@@@WSParameters", requestParams.toString());
        return requestParams;
    }

    @Override
    public String getBodyContentType() {
        if (mHttpEntity != null) {
            return mHttpEntity.getContentType().getValue();
        }
        return null;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (mHttpEntity != null) {
                Log.d("request: multipart", mHttpEntity.toString());
                mHttpEntity.writeTo(bos);
            } else {
                return null;
            }
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
//            Log.v("@DATA", jsonString);
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(Object response) {
        responseListener.onResponse(response);
    }

    /**
     * A method used to get mime type of given file path of image or video.
     *
     * @param filePath
     * @return {@link String}
     */
    public String getMimeType(String filePath) {
        String type = null;
        String extension = getFileExtensionFromUrl(filePath);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * A method used to get extension type of given file path of image or video.
     *
     * @param url
     * @return {@link String}
     */
    public String getFileExtensionFromUrl(String url) {
        int dotPos = url.lastIndexOf('.');
        if (0 <= dotPos) {
            return (url.substring(dotPos + 1)).toLowerCase();
        }
        return "";
    }
}