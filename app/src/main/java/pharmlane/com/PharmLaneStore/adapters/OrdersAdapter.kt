package pharmlane.com.PharmLaneStore.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.activities.CustomOrderDetailActivity
import pharmlane.com.PharmLaneStore.activities.CustomViewOfferActivity
import pharmlane.com.PharmLaneStore.activities.OrderDetailActivity
import pharmlane.com.PharmLaneStore.response.GetStoreListResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OrdersAdapter(
        var context: Context,
        var directOrderdata: List<GetStoreListResponse.DirectOrder>
) : RecyclerView.Adapter<OrdersAdapter.MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersAdapter.MyHolder {
        val v =
                LayoutInflater.from(parent.context).inflate(R.layout.item_orders_viewall, parent, false)
        return OrdersAdapter.MyHolder(v)
    }

    override fun getItemCount(): Int {
        return directOrderdata.size
    }

    override fun onBindViewHolder(holder: OrdersAdapter.MyHolder, position: Int) {

        var isMoreHour = "0"
        var statusOfOrder = "offersmade"


        if (directOrderdata[position].requestStatus.equals(">24 hrs", ignoreCase = true)) {
            isMoreHour = "1"
        }


        if (directOrderdata[position].orderStatus.equals("Invoiced", ignoreCase = true)) {
            statusOfOrder = "uploadinvoice"
        }
        if (directOrderdata[position].orderStatus.equals("Accepted", ignoreCase = true) && directOrderdata[position].requestStatus.equals(">24 hrs", ignoreCase = true)) {
            holder.simpleChronometer.visibility = View.GONE
            holder.txtOfferAccepted.visibility = View.VISIBLE
        } else if (directOrderdata[position].orderStatus.equals("Accepted", ignoreCase = true)) {
            holder.simpleChronometer.visibility = View.GONE
            holder.txtOfferAccepted.visibility = View.VISIBLE
        } else if (directOrderdata[position].requestStatus.equals(">24 hrs", ignoreCase = true)) {
            holder.simpleChronometer.visibility = View.VISIBLE
            holder.txtOfferAccepted.visibility = View.GONE
        } else {
            holder.simpleChronometer.visibility = View.VISIBLE
            holder.txtOfferAccepted.visibility = View.GONE
        }



        holder.txtDatenTime.text = directOrderdata.get(position).createdDate
        holder.txtId.text = "ID: " + directOrderdata.get(position).orderNo
        holder.txtStatus.text = directOrderdata.get(position).orderStatus
        holder.txtStatus1.text = directOrderdata.get(position).requestStatus

        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US)
        val cal1 = Calendar.getInstance()
        val currDate1 = dateFormat.format(cal1.time)
        cal1.time = dateFormat.parse(directOrderdata[position].createdDate.toString())
        cal1.get(Calendar.HOUR_OF_DAY)
        val convertedDate1 = dateFormat.format(cal1.time)

        val stopTime1 = dateFormat.parse(convertedDate1)
        val startTime1 = dateFormat.parse(currDate1)
        val difference1 = startTime1.time - stopTime1.time

        val InSec1 = TimeUnit.MILLISECONDS.toSeconds(difference1) % 60
        val day1 = TimeUnit.MILLISECONDS.toDays(difference1)
        val hh1 = (TimeUnit.MILLISECONDS.toHours(difference1) - TimeUnit.DAYS.toHours(day1))
        val mm1 = (TimeUnit.MILLISECONDS.toMinutes(difference1) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(difference1)
        ))


        DrawableCompat.setTint(
                holder.txtStatus1.background,
                ContextCompat.getColor(
                        context,
                        AppConstant.setsStatus(directOrderdata[position].requestStatus!!)
                )
        )

        DrawableCompat.setTint(
                holder.txtStatus.getBackground(),
                ContextCompat.getColor(
                        context,
                        AppConstant.setsStatus(directOrderdata[position].orderStatus!!)
                )
        )


        try {
            holder.simpleChronometer.base = stopTime1.time
            holder.simpleChronometer.onChronometerTickListener = Chronometer.OnChronometerTickListener {
                var t: Long = System.currentTimeMillis() - holder.simpleChronometer.base
                val days = TimeUnit.MILLISECONDS.toDays(t)
                t -= TimeUnit.DAYS.toMillis(days)
                val hours = TimeUnit.MILLISECONDS.toHours(t)
                t -= TimeUnit.HOURS.toMillis(hours)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(t)
                t -= TimeUnit.MINUTES.toMillis(minutes)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(t)
                var stopwatchDisplay = "%dd %dh %dm %ds"
                stopwatchDisplay = String.format(stopwatchDisplay, days, hours, minutes, seconds)
                holder.simpleChronometer.text = "Time Elapsed: " + stopwatchDisplay
            }
            holder.simpleChronometer.start()
        } catch (e: ParseException) {
            e.printStackTrace()
        }




        holder.timer = ("" + hh1 + "h:" + mm1 + "m")

        holder.itemView.setOnClickListener {
            if (directOrderdata.get(position).orderStatus.equals("Pending", ignoreCase = true)) {
                if (directOrderdata.get(position).orderTypeName.equals(
                                "Custom",
                                ignoreCase = true
                        )
                ) {

                    Log.e("HELLOCLICKS", "IFFF   ")
                    context.startActivity(
                            Intent(context, CustomOrderDetailActivity::class.java)
                                    .putExtra("timer", holder.timer)
                                    .putExtra("direct", "1")
                                    .putExtra("moreHour", isMoreHour)
                                    .putExtra(
                                            "statusviewall",
                                            "1"
                                    )
                                    .putExtra("orderId", directOrderdata[position].orderId.toString())
                    )
                    // .putExtra("orderId", "6"))
                } else {
                    Log.e("HELLOCLICKS", "ELSE   ")
                    context.startActivity(
                            Intent(context, OrderDetailActivity::class.java)
                                    .putExtra("moreHour", isMoreHour)
                                    .putExtra("timer", holder.timer)
                                    .putExtra("direct", "1")
                                    .putExtra("orderId", directOrderdata[position].orderId.toString())
                    )

                }
            } else {
                Log.e("HELLOCLICKS", "ELSE MAIN   ")
                context.startActivity(
                        Intent(context, CustomViewOfferActivity::class.java)
                                .putExtra(statusOfOrder, "1")
                                .putExtra("custom", directOrderdata.get(position).orderTypeName)
                                .putExtra("orderId", directOrderdata.get(position).orderId.toString())
                )
            }
        }

        holder.imgPrompt.setOnClickListener {

        }
    }

    class MyHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        val txtId = itemView.findViewById(R.id.txt_id) as AppCompatTextView
        val txtStatus1 = itemView.findViewById(R.id.txt_status1) as AppCompatTextView
        val txtStatus = itemView.findViewById(R.id.txt_status) as AppCompatTextView
        val txtDatenTime = itemView.findViewById(R.id.txt_dtntime) as AppCompatTextView
        val txtTimeElapsed = itemView.findViewById(R.id.txt_time_elapsed) as AppCompatTextView
        val simpleChronometer = itemView.findViewById(R.id.simpleChronometer) as Chronometer

        val txtOfferAccepted = itemView.findViewById(R.id.txt_ofer_accepted) as AppCompatTextView
        val imgPrompt = itemView.findViewById(R.id.img_prompt) as AppCompatImageView
        var timer = ""
    }

    private fun diffTime(
            datenTime: String,
            holder: MyHolder
    ) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US)
        val createdConvertedDate = dateFormat.parse(datenTime)
        val date = SimpleDateFormat("dd MMM,yyyy",Locale.US).format(createdConvertedDate)
        val time = SimpleDateFormat("hh:mm a",Locale.US).format(createdConvertedDate).lowercase()
        holder.txtDatenTime.text = (date + " " + time)

        val cal = Calendar.getInstance()
        val currDate = dateFormat.format(cal.time).lowercase()
        cal.time = dateFormat.parse(datenTime)
        cal.add(Calendar.DATE, 1)
        val convertedDate = dateFormat.format(cal.time)

        Log.d("TAG", "currDate--orderrr--" + currDate)

        val startTime = dateFormat.parse(convertedDate)
        val stopTime = dateFormat.parse(currDate)

        val difference = startTime.time - stopTime.time

        val diffInSec = difference / 1000
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        val min =
                (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60)

        val hourss = if (hours < 0) -hours else hours

        val InSec = TimeUnit.MILLISECONDS.toSeconds(difference) % 60
        val day = TimeUnit.MILLISECONDS.toDays(difference)
        val hh = (TimeUnit.MILLISECONDS.toHours(difference) - TimeUnit.DAYS.toHours(day))
        val mm = (TimeUnit.MILLISECONDS.toMinutes(difference) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(difference)
        ))


        holder.timer = ("" + hh + "h:" + mm + "m:" + InSec + "s")
        // holder.txtTimeElapsed.setText("Time Elapsed: " + hh + "h " + mm + "m " + InSec + "s")

        val cal1 = Calendar.getInstance()
        val currDate1 = dateFormat.format(cal1.time)
        cal1.time = dateFormat.parse(datenTime)
        //cal1.get(Calendar.HOUR_OF_DAY)
        val convertedDate1 = dateFormat.format(cal1.time)


        val stopTime1 = dateFormat.parse(convertedDate1)
        val startTime1 = dateFormat.parse(currDate1)

        val difference1 = startTime1.time - stopTime1.time


        val InSec1 = TimeUnit.MILLISECONDS.toSeconds(difference1) % 60
        val day1 = TimeUnit.MILLISECONDS.toDays(difference1)
        val hh1 = (TimeUnit.MILLISECONDS.toHours(difference1) - TimeUnit.DAYS.toHours(day1))
        val mm1 = (TimeUnit.MILLISECONDS.toMinutes(difference1) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(difference1)
        ))
        //holder.txtTimeElapsed.setText("Time Elapsed: " +day1 + "d " +  hh1 + "h " + mm1 + "m " + InSec1 + "s")
        holder.txtTimeElapsed.text = "Time Elapsed: " + day1 + "d " + hh1 + "h " + mm1 + "m "

    }
}