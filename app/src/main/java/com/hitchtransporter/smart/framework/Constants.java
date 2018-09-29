package com.hitchtransporter.smart.framework;

public interface Constants {

    String TAG = "Universal Log TAG======";

    String IS_LOGGED_IN = "is_logged_in";
    String ENGLISH_STR = "en";
    String GERMAN_STR = "de";
    String FINNISH_STR = "fi";
    String SWEDISH_STR = "sv";
    String ENGLISH_CODE = "1";
    String GERMAN_CODE = "2";
    String SWEDISH_CODE = "3";
    String FINNISH_CODE = "4";

    String SELECTED_LOCALE = "selected_local";


    int PERMISSIONS_REQUEST_CODE = 501;
    int SELECT_AREA = 101;
    int CAMERA_PROFILE_SOURCE = 234;
    int GALLERY_PROFILE_SOURCE = 235;
    int CAMERA_CAR_SOURCE = 236;
    int GALLERY_CAR_SOURCE = 237;


    /*-----------------SharedPreferences-----------*/
    String SP_COOKIES = "Set-Cookie";
    String SP_LOGGED_USER = "loggedUser";
    String SP_HITCHER_COUNTRY = "hitcherCountry";
    String SP_USER_LATITUDE = "userLatitude";
    String SP_USER_LONGITUDE = "userLongitude";
    String SP_USER_ID = "getUserId";
    String SP_FIREBASE_REGID = "firebaseRegId";
    String NO_USER_ID = "no_user_id";
    String CHAT_NOTIFICATION = "chat_notification";
    String CURRENT_OPPONENT_ID = "current_opponent_id";


    /* --------------Web Services-----------------*/
    String GET_PACKS = "get_packs";
    String UPDATE_PROFILE = "update_profile";


    /* --------------TAGS--------------------*/

    int STATUS_SUCCESS = 1;
    int STATUS_FAIL = 0;

    String DATA = "data";
    String USER_TYPE = "user_type";

    String SET_PAGE = "set_page";
    String IS_SWITCH = "is_switch";

    String PROFILE = "profile";
    String HOME = "home";
    String ORDER_HISTORY = "order_history";

    String USER_ID = "user_id";
    String TO_USER_ID = "to_user_id";
    String U_ID = "u_id";
    String USERNAME = "username";
    String USER_NAME = "user_name";
    String FIRST_NAME = "first_name";
    String LAST_NAME = "last_name";
    String GENDER = "gender";
    String EMAIL = "email";
    String PASSWORD = "password";
    String IMAGE = "image";
    String NOTIFICATIONS = "notifications";
    String NO_DATA = "no_data";
    String NOTIFICATION_TOKEN = "notification_token";
    String DEVICE_UID = "device_uid";
    String MOBILE = "mobile";
    String USER_TYPE_CODE = "user_type_code";
    String USER_CODE = "0";
    String TRANSPORTER_CODE = "1";
    String ANDROID_CODE = "3";
    String TITLE = "title";
    String ADDRESS = "address";
    String ADDRESS_LINE_1 = "address_line_1";
    String STREET = "street";
    String AREA = "area";
    String CITY = "city";
    String STATE = "state";
    String COUNTRY = "country";
    String COUNTRY_CODE = "country_code";
    String TRANSPORTER_TYPE = "transporter_type";
    String LATITUDE = "latitude";
    String IS_ONLINE = "is_online";
    String LONGITUDE = "longitude";
    String CURRENT_LATITUDE = "current_latitude";
    String CURRENT_LONGITUDE = "current_longitude";
    String ADDRESS_ARRAY = "address_array";
    String DEVICE_TOKEN = "device token";
    String DEVICE_TYPE = "device type";
    String MESSAGE = "message";
    String ANDROID = "android";
    String APPROVE_REJECT = "approve_reject";
    String APPROVE = "approve";
    String REJECT = "reject";
    String APPROVE_CODE = "1";
    String REJECT_CODE = "2";
    String ORDER_ID = "order_id";
    String ORDER_REQUEST_ID = "order_request_id";
    String FILTER_TYPE = "filter_type";
    String PRICE_CODE = "1";
    String DISTANCE_CODE = "0";
    String IS_BANK_DETAIL = "is_bank_detail";
    String USER_RANGE = "user_range"; //Actually transporter range only

    String IS_DROPOFF = "is_dropoff";

    String ORDER_ID_FOR_TRACKING = "order_id_for_tracking";
    String BROADCAST_MESSAGE = "broadcast_message";

    String SOCIAL_TYPE = "social_type";
    String SOCIAL_ID = "social_id";
    String HITCHER_TYPE = "transporter_type";

    String NOTIFICATION_DETAIL = "notification_detail";
    String NOTIFICATION_TITLE = "notification_title";
    String NOTIFICATION_DESCRIPTION = "notification_description";
    String VIEW_DATE = "view_date";

    String RATING_DETAIL = "rating_detail";
    String AVERAGE_DETAIL = "average_detail";

    String USER = "User";
    String TRANSPORTER = "Transporter";
    String HITCHER = "Hitcher";

    String CHAT_ID = "chat_id";


    String TRANSPORTER_ID = "transporter_id";
    String TRANSPORTER_NAME = "transporter_name";
    String TRANSPORTER_IMAGE = "transporter_image";
    String PROFILE_IMAGE = "profile_image";
    String PROFILE_DETAIL = "profile_detail";
    String RATE_PER_KM = "rate_per_km";
    // String SIZE_OF_BOX = "size_of_box";
    String BOX_WIDTH = "box_width";
    String BOX_LENGTH = "box_length";
    String BOX_HEIGHT = "box_height";
    String BOX_WEIGHT = "box_weight";
    String PICKUP_ADDRESS = "pickup_address";
    String PICKUP_LATITUDE = "pickup_latitude";
    String PICKUP_LONGITUDE = "pickup_longitude";
    String DROPOFF_ADDRESS = "dropoff_address";
    String DROPOFF_LATITUDE = "dropoff_latitude";
    String DROPOFF_LONGITUDE = "dropoff_longitude";
    String ORDER_DATETIME = "order_datetime";
    String LANGUAGE = "language";

    String HEIGHT = "height";
    String WEIGHT = "weight";
    String WIDTH = "width";
    String LENGTH = "length";
    String SIZE = "size";
    String DISTANCE = "distance";
    String TIME = "time";
    String DURATION = "duration";
    String AMOUNT = "amount";
    String VAT_PERCENT = "vat_percent";
    String APP_FEE = "app_fee";
    String APP_FEE_VAT = "app_fee_vat";

    String REFUND_AMOUNT = "refund_amount";
    String FINAL_AMOUNT = "final_amount";
    String RATING = "rating";
    String AVG_RATING = "avg_rating";
    String USER_REVIEW = "user_review";
    String RATING_INSERTED_DATE = "rating_inserted_date";


    String VAT_PERCENT_H = "vat_percent";
    String VAT_AMOUNT = "vat_amount";

    String DESCRIPTION = "description";
    String IS_RATING = "is_rating";
    String IS_COMPLAIN = "is_complain";
    String NUMBER_PLATE = "number_plate";
    String DATETIME = "datetime";
    String DISTANCE_RANGE = "distance_range";
    String PRICE = "price";


    String TOKEN = "token";
    String IS_FAST_CHECKOUT = "is_fast_checkout";
    String CARD_NUMBER = "card_number";
    String EXPIRY_MONTH = "expiry_month";
    String EXPIRY_YEAR = "expiry_year";
    String CVV = "cvv";

    String LOCAL = "local";
    String WEB = "web";

    String FACEBOOK_CODE = "1";
    String GOOGLE_CODE = "2";
    String TWITTER_CODE = "3";
    String ID = "id";
    String SOCIAL_DATA = "social_data";
    String TYPE = "type";

    //-----------------NOTIFICATION TYPES-------------------------
    String ORDER_REQUEST = "order_request";
    String APPROVE_ORDER = "approve_order";
    String REJECT_ORDER = "reject_order";
    String ORDER_PAYMENT = "order_payment";
    String ORDER_DELIVERY_START = "order_delivery_start";
    String ORDER_DELIVERY_COMPLETE = "order_delivery_complete";

    String PAYMENT_DATE = "payment_date";

    String NEW_ORDER = "new_order";
    String ORDER_APPROVE = "order_approve";
    String ORDER_REJECT = "order_reject";
    String ORDER_START = "order_start";
    String ORDER_COMPLETE = "order_complete";
    String ORDER_CANCEL = "order_cancel";

    String ORDER_STATUS = "order_status";
    String IS_PAY = "is_pay";
    String FOR_TODAY = "for_today";
    String IS_NEARBY = "is_nearby";
    String IS_NEARBY_FINISH = "is_nearby_finish";


    String STATUS_PENDING = "0";
    String STATUS_APPROVED = "1";
    String STATUS_REJECTED = "2";
    String STATUS_STARTED = "3";
    String STATUS_COMPLETED = "4";
    String STATUS_CANCELLED = "5";

    String STATUS_PAID = "1";
    String STATUS_UNPAID = "0";


    String IS_INITIAL = "is_initial";

    String REMOVED_IMAGES = "removed_images";

    String EXTRA_LARGE = "Extra Large";
    String LARGE = "Large";
    String MEDIUM = "Medium";
    String SMALL = "Small";
    String EXTRA_SMALL = "Extra Small";

    String XS = "XS";
    String S = "S";
    String M = "M";
    String L = "L";
    String XL = "XL";

    String Cm = "Cm";
    String Km = "Km";
    String € = "€";
    String $ = "$";
    String Kg = "Kg";

    String CHAT_DIALOG = "chat_dialog";
    String OPPONENT_NAME = "opponent_name";

    String FROM_DATE = "from_date";
    String TO_DATE = "to_date";

    String START_JOB_HOME = "start_job_home";
    //-----------------BANK DETAILS-------------------------
    String ACCOUNT_NUMBER = "account_number";
    String POSTAL_CODE = "postal_code";
    String DOB = "dob";
    String IBAN = "iban";
    String CURRENCY = "currency";
    String BANK_TOKEN = "bank_token";

    String EUR_CURRENCY = "EUR";
    String FINLAND_CODE = "FI";
    String SWEDEN_CODE = "SE";
    String GERMANY_CODE = "DE";


    //{country: 'Germany', code: 'DE'},Sent on:2:53 pmFrom:Pankaj_ios{country: 'Finland', code: 'FI'},From:Pankaj_ios{country: 'Sweden', code: 'SE'},


    String QUICK_BLOX_USER_ID = "qckblx_userId";
    String QUICK_BLOX_PASSWORD = "qckblx123";

    //-----------------CHAT -------------------------
    String SENDER_ID = "sender_id";
    String RECEIVER_ID = "receiver_id";
    String RECEIVER_EMAIL = "receiver_email";
    String MSG = "msg";
    String SENDER_NAME = "sender_name";

    String IS_LEGAL = "is_legal";

    String ONGOING_ORDERS = "ongoing_orders";
    String REST_ORDERS = "rest_orders";

}
