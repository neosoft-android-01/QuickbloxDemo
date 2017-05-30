package com.webwerks.quickbloxdemo.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by webwerks on 27/4/17.
 */

public class DateUtils {


    public static String getDurationFromMilliseconds(int milliseconds){
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) milliseconds),
                TimeUnit.MILLISECONDS.toSeconds((long) milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) milliseconds)));
    }

    public static String getTimeText( long dateTime, String formatFor ) {

        Calendar today        = Calendar.getInstance();
        Calendar previousday  = Calendar.getInstance();//yest
        Calendar previousday1 = Calendar.getInstance();//dy before yest
        Calendar previousday2 = Calendar.getInstance();//2dys before yest
        Calendar previousday3 = Calendar.getInstance();//3dys before yest
        Calendar previousday4 = Calendar.getInstance();//4dys before yest
        Calendar previousday5 = Calendar.getInstance();//5dys before yest
        previousday.add( Calendar.DATE, -1 );
        previousday1.add( Calendar.DATE, -2 );
        previousday2.add( Calendar.DATE, -3 );
        previousday3.add( Calendar.DATE, -4 );
        previousday4.add( Calendar.DATE, -5 );
        previousday5.add( Calendar.DATE, -6 );

        DateFormat formatter  = new SimpleDateFormat( "hh:mm a" );
        DateFormat formatter1 = new SimpleDateFormat( "dd-MM-yyyy" );
        DateFormat formatter2 = new SimpleDateFormat( "EEE" );//"EEEE hh:mm a"
        if ( formatFor.equalsIgnoreCase( "chat" ) ) {
            formatter = new SimpleDateFormat( "hh:mm a" );
            formatter1 = new SimpleDateFormat( "dd/MM/yyyy hh:mm a" );
            formatter2 = new SimpleDateFormat( "EEE hh:mm a" );//""
        }

        if(dateTime%1000!=0){
            dateTime = dateTime*1000;

        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( dateTime );
        //Log.e( "calendar", calendar.getTime() + "" );
        if ( getZeroTimeDate( calendar.getTime() ).compareTo( getZeroTimeDate( today.getTime() ) ) == 0 ) {
            return formatter.format( calendar.getTime() );
        }
        else if ( getZeroTimeDate( calendar.getTime() ).compareTo( getZeroTimeDate( previousday.getTime() ) ) == 0
                || getZeroTimeDate( calendar.getTime() ).compareTo( getZeroTimeDate( previousday1.getTime() ) ) == 0
                || getZeroTimeDate( calendar.getTime() ).compareTo( getZeroTimeDate( previousday2.getTime() ) ) == 0
                || getZeroTimeDate( calendar.getTime() ).compareTo( getZeroTimeDate( previousday3.getTime() ) ) == 0
                || getZeroTimeDate( calendar.getTime() ).compareTo( getZeroTimeDate( previousday4.getTime() ) ) == 0
                || getZeroTimeDate( calendar.getTime() ).compareTo( getZeroTimeDate( previousday5.getTime() ) ) == 0
                ) {
            return formatter2.format( calendar.getTime() );
        }
        else {
            return formatter1.format( calendar.getTime() );
        }
    }

    private static Date getZeroTimeDate( Date fecha ) {
        Date     res      = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime( fecha );
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        calendar.set( Calendar.MILLISECOND, 0 );

        res = calendar.getTime();

        return res;
    }
}
