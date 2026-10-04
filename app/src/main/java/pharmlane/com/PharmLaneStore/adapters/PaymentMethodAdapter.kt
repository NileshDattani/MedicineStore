package pharmlane.com.PharmLaneStore.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.response.AllStoreSubPaymentResponse
import pharmlane.com.PharmLaneStore.utills.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class PaymentMethodAdapter(
        var context: Context,
        var paymentList: List<AllStoreSubPaymentResponse.Datum>,
        var paymntmode: Paymentmode
) : RecyclerView.Adapter<PaymentMethodAdapter.MyHolder>() {
    var selected_position = -1

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_payment_methods, parent, false)
        return MyHolder(v)
    }

    override fun getItemCount(): Int {

        return paymentList.size
    }

    override fun onBindViewHolder(holder: MyHolder, pos: Int) {
        if (pos == selected_position) {
            holder.txt.setTextColor(ContextCompat.getColor(context, R.color.black_txt))
            holder.txt.isChecked = true
        } else {
            holder.txt.setTextColor(ContextCompat.getColor(context, R.color.grey_txt))
            holder.txt.isChecked = false
        }
        if (!paymentList.get(pos).upi_id.equals("")) {
            holder.txtUpi.visibility = View.VISIBLE
            holder.img_qr_code.visibility = View.VISIBLE

        } else {
            holder.txtUpi.visibility = View.GONE
            holder.img_qr_code.visibility = View.GONE
        }
        Log.d("TAG", "ADD===A-->" + paymentList.get(pos))
//        holder.txt.text = paymentList.get(pos).name
        holder.txt_paymnt_name.text = paymentList.get(pos).name
        holder.txtUpi.setText(paymentList.get(pos).upi_id)


        Glide.with(context).load(paymentList[pos].qr_img)
                .placeholder(R.drawable.ic_launcher_background)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(@Nullable e: GlideException?, model: Any?, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        Log.e("HElloDATA", "   onLoadFailed   " + paymentList.get(pos).name)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable?>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        Log.e("HElloDATA", "   onResourceReady   " + paymentList.get(pos).name)

                        return false
                    }
                }).into(holder.img_qr_code)

        holder.txt.setOnClickListener {


            paymntmode.getId(paymentList.get(pos).name!!, paymentList.get(pos).upi_id!!)

            selected_position = pos
            notifyDataSetChanged()
        }

        holder.btnDownloadQr.setOnClickListener {
            val imageName = paymentList[pos].qr_img!!.substring(paymentList[pos].qr_img!!.lastIndexOf('/') + 1)

            if (Utils.downloadImageNew(imageName, paymentList[pos].qr_img!!, context)) {
                Toast.makeText(context, "The QR Code saved in your Pictures folder.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "he QR Code download failed.", Toast.LENGTH_SHORT).show()
            }

        }


    }

    class MyHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val txt = itemView.findViewById(R.id.checkBox) as CheckBox
        val txt_paymnt_name = itemView.findViewById(R.id.txt_paymnt_name) as AppCompatTextView
        val txtUpi = itemView.findViewById(R.id.edt_upi_id) as AppCompatEditText
        val btnDownloadQr = itemView.findViewById(R.id.btn_download_qr) as AppCompatTextView
        val img_qr_code = itemView.findViewById(R.id.img_edit_qr_code) as AppCompatImageView
    }

    interface Paymentmode {
        fun getId(name: String, id: String)
    }

}