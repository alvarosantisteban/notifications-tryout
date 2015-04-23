package com.alvarosantisteban.notificationtryout;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {

    private static final int NOTIFICATION_ID = 7;
    private int mNumNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null && intentExtras.containsKey(Constants.INTENT_EXTRA_CLICKED_NOTIFICATION)) {
            /*
             Now I dont need to check the content of the extra, because I just have one Notification
             If i did, I would check if
             intentExtras.getInt(Constants.INTENT_EXTRA_CLICKED_NOTIFICATION);
             returns the NOTIFICATION_ID
              */

            // The user came from clicking the notification, reset the number of notifications
            mNumNotifications = 0;
        } else{
            // Get the number of sent notifications
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
            mNumNotifications = sharedPreferences.getInt(Constants.SHARED_PREF_NUM_NOTIFICATIONS, 0);
        }
    }

    public void createNotification(View v) {
        createNotification();
    }

    /**
     * Creates a Notification with random text and sets the MainActivity as target. The Notification can be identified
     * under the ID NOTIFICATION_ID.
     */
    public void createNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(getString(R.string.notification_text))
                        .setNumber(++mNumNotifications)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        // Sets an expanded layout into the notification object.
        mBuilder.setStyle(addExpandedLayout());

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(Constants.INTENT_EXTRA_CLICKED_NOTIFICATION, NOTIFICATION_ID);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // NOTIFICATION_ID allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * Creates an expanded Inbox style notification with 4 lines and a title text
     * @return the InboxStyle notification with 4 lines and a title text
     */
    private NotificationCompat.InboxStyle addExpandedLayout() {
        // Expanded layout of notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[4];
        events[0] = getString(R.string.notification_first_line);
        events[1] = getString(R.string.notification_second_line);
        events[2] = getString(R.string.notification_third_line);
        events[3] = getString(R.string.notification_forth_line);
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle(getString(R.string.notification_expanded_title));
        // Moves events into the expanded layout
        for (String event : events) {
            inboxStyle.addLine(event);
        }

        return inboxStyle;
    }

    public void goToSecond(View view) {
        Intent goToSecondIntent = new Intent(this, SecondActivity.class);
        startActivity(goToSecondIntent);
    }

    /////////////////////////////////////////////////////////////////////
    // LIFE CYCLE
    /////////////////////////////////////////////////////////////////////

    @Override
    protected void onPause(){
        super.onPause();

        // Store the number of sent Notifications
        SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).edit();
        editor.putInt(Constants.SHARED_PREF_NUM_NOTIFICATIONS, mNumNotifications);
        editor.apply();
    }

    /////////////////////////////////////////////////////////////////////
    // MENU
    /////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
