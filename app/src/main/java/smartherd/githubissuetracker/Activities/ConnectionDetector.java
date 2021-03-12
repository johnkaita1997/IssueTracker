package smartherd.githubissuetracker.Activities;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by John Kaita on 11/30/2018.
 */

public class ConnectionDetector {

    Context context;

    public ConnectionDetector(Context context) {

        this.context = context;
    }

    public boolean isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {

                if (info.getState() == NetworkInfo.State.CONNECTED) {

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Method for parsing imported data and storing in ArrayList<XYValue>
     */
    public void parseStringBuilder(StringBuilder mStringBuilder) {

        // splits the sb into rows.
        String[] rows = mStringBuilder.toString().split(":");

        //Add to the ArrayList<XYValue> row by row
        for (int i = 0; i < rows.length; i++) {
            //Split the columns of the rows
            String[] columns = rows[i].split(",");

            //use try catch to make sure there are no "" that try to parse into doubles.
            try {
                double x = Double.parseDouble(columns[0]);
                double y = Double.parseDouble(columns[1]);

                String cellInfo = "(x,y): (" + x + "," + y + ")";

                //add the the uploadData ArrayList
                //uploadData.add(new XYValue(x,y));

            } catch (NumberFormatException e) {
            }
        }

        //printDataToLog();
    }
}
