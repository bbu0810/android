package com.hitchtransporter.smart.framework;

import android.content.DialogInterface;

public interface AlertMagnatic {

	public abstract void PositiveMethod(DialogInterface dialog, int id);
	public abstract void NegativeMethod(DialogInterface dialog, int id);
}
