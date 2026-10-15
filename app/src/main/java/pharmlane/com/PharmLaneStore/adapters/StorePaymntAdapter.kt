package pharmlane.com.PharmLaneStore.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.text.TextUtils
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
import androidx.recyclerview.widget.RecyclerView
import pharmlane.com.PharmLaneStore.R
import pharmlane.com.PharmLaneStore.model.Register
import pharmlane.com.PharmLaneStore.response.GetPaymentMethodsResponse
import pharmlane.com.PharmLaneStore.utills.AppConstant
import pharmlane.com.PharmLaneStore.utills.AppConstant.convert
import pharmlane.com.PharmLaneStore.utills.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputLayout
import org.w3c.dom.Text
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL


class StorePaymntAdapter(
        var context: Context,
        var paymentArray: List<GetPaymentMethodsResponse.Data>,
        var arrapaymnt: arrayPayment, var qrImg: addQRCode, var isProgressShow: isProgress) : RecyclerView.Adapter<StorePaymntAdapter.MyHolder>() {

    var array = ArrayList<Register.payment>()
    var payMents = Register.payment()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_paymnt, parent, false)
        return StorePaymntAdapter.MyHolder(v)
    }

    override fun getItemCount(): Int {
        return paymentArray.size
    }

    override fun onBindViewHolder(holder: MyHolder, pos: Int) {


        holder.txtPaymntName.text = paymentArray[pos].method_name
        holder.txtNote.text = "Check to Save "+paymentArray[pos].method_name



        holder.edtUpiId.setText(paymentArray[pos].upi_id)




        if (!paymentArray[pos].qr_img.equals("")) {

            holder.btnDownloadQR.visibility = View.VISIBLE
            holder.btnEditQR.visibility = View.VISIBLE
            holder.btnUploadQR.visibility = View.GONE

//            Glide.with(context).load(paymentArray[pos].qr_img).placeholder(R.drawable.upload_picture_box).into(holder.img_qr_code)

            Glide.with(context).load(paymentArray[pos].qr_img)
                    .placeholder(R.drawable.upload_picture_box)
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(@Nullable e: GlideException?, model: Any?, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable?>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            Log.e("myisSelected", "isSelected-->  " + paymentArray[pos].isSelected)

                            return false
                        }
                    }).into(holder.img_qr_code)

        }else{
            holder.btnDownloadQR.visibility = View.GONE
            holder.btnEditQR.visibility = View.GONE
            holder.btnUploadQR.visibility = View.VISIBLE
        }


//        if (paymentArray[pos].isInput.equals("0")) {
//            holder.txtview.visibility = View.GONE
//            holder.view.visibility = View.GONE
//        } else {
//            holder.txtview.visibility = View.VISIBLE
//            holder.view.visibility = View.VISIBLE
//        }


        holder.img_qr_code.setOnClickListener {

//            if (paymentArray[pos].method_name.toString().equals("Google Pay")) {
//                AppConstant.setPreferenceText(AppConstant.GPAY_QR_CODE, "")
//            } else if (paymentArray[pos].method_name.toString().equals("Paytm")) {
//                AppConstant.setPreferenceText(AppConstant.PAYTM_QR_CODE, "")
//            } else if (paymentArray[pos].method_name.toString().equals("Phone Pe")) {
//                AppConstant.setPreferenceText(AppConstant.PHONEPAY_QR_CODE, "")
//
//            } else {
//                Log.e("HeLLOQQUU", "hoo")
//            }
//
//            qrImg.qr_codee(holder.img_qr_code, holder.checkBox, paymentArray[pos].method_name.toString())

        }

        holder.btnEditQR.setOnClickListener {

            if (paymentArray[pos].method_name.toString().equals("Google Pay")) {
                AppConstant.setPreferenceText(AppConstant.GPAY_QR_CODE, "")
            } else if (paymentArray[pos].method_name.toString().equals("Paytm")) {
                AppConstant.setPreferenceText(AppConstant.PAYTM_QR_CODE, "")
            } else if (paymentArray[pos].method_name.toString().equals("Phone Pe")) {
                AppConstant.setPreferenceText(AppConstant.PHONEPAY_QR_CODE, "")

            } else {
                Log.e("HeLLOQQUU", "hoo")
            }

            qrImg.qr_codee(holder.img_qr_code, holder.checkBox, paymentArray[pos].method_name.toString())

        }


        if (paymentArray[pos].method_name.equals("On Credit")) {
            holder.img_qr_code.visibility = View.GONE
            holder.edtUpiId.visibility = View.GONE
            holder.view.visibility = View.GONE
            holder.btnDownloadQR.visibility = View.GONE
        }


        holder.btnUploadQR.setOnClickListener {
            if (paymentArray[pos].method_name.toString().equals("Google Pay")) {
                AppConstant.setPreferenceText(AppConstant.GPAY_QR_CODE, "")
            } else if (paymentArray[pos].method_name.toString().equals("Paytm")) {
                AppConstant.setPreferenceText(AppConstant.PAYTM_QR_CODE, "")
            } else if (paymentArray[pos].method_name.toString().equals("Phone Pe")) {
                AppConstant.setPreferenceText(AppConstant.PHONEPAY_QR_CODE, "")

            } else {
                Log.e("HeLLOQQUU", "hoo")
            }

            qrImg.qr_codee(holder.img_qr_code, holder.checkBox, paymentArray[pos].method_name.toString())
        }
        holder.btnDownloadQR.setOnClickListener {
            val imageName = paymentArray[pos].qr_img!!.substring(paymentArray[pos].qr_img!!.lastIndexOf('/') + 1)

            if (Utils.downloadImageNew(imageName, paymentArray[pos].qr_img!!, context)) {
                Toast.makeText(context, "The QR Code saved in your Pictures folder.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "he QR Code download failed.", Toast.LENGTH_SHORT).show()
            }

        }

        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            isProgressShow.showProgress(true)

            if (isChecked) {


                Log.e("HElloPAYments", "PAy ID   " + holder.txtPaymntName.text)

                payMents = Register.payment()
                payMents.payment_method_id = paymentArray[pos].paymentMethodId
                payMents.is_selected = "1"


                if (!paymentArray[pos].qr_img.equals("") && AppConstant.getPreferenceText(AppConstant.GPAY_QR_CODE).equals("")) {
                    payMents.upi_id = paymentArray[pos].upi_id
                    payMents.qr_img = ""
                    array.add(payMents)
                    arrapaymnt.add(array)
//                    myTask(paymentArray[pos].qr_img!!, holder).execute()
//                    payMents.qr_img = "Test"//
//                    array.add(payMents)
//                    arrapaymnt.add(array)
//                    isProgressShow.showProgress(false)
                } else {

                    payMents.upi_id = holder.edtUpiId.text.toString()
                    if (paymentArray[pos].method_name.toString().equals("Google Pay")) {
                        payMents.qr_img = AppConstant.getPreferenceText(AppConstant.GPAY_QR_CODE)
                    } else if (paymentArray[pos].method_name.toString().equals("Paytm")) {
                        payMents.qr_img = AppConstant.getPreferenceText(AppConstant.PAYTM_QR_CODE)
                    } else if (paymentArray[pos].method_name.toString().equals("Phone Pe")) {
                        payMents.qr_img = AppConstant.getPreferenceText(AppConstant.PHONEPAY_QR_CODE)

                    } else {
                        Log.e("HeLLOQQUU", "hoo")
                    }


//                if (paymentArray[pos].isInput.equals("1") && holder.edtUpiId.text.toString().equals("")) {
//                    Toast.makeText(context, "Add upi id", Toast.LENGTH_SHORT).show()
//                    holder.checkBox.isChecked = false
//                } else


                    if (paymentArray[pos].method_name.equals("On Credit")) {
                        payMents.payment_method_id = paymentArray[pos].paymentMethodId
                        payMents.upi_id = ""
                        payMents.qr_img = ""
                        payMents.is_selected = "1"
                        array.add(payMents)
                        arrapaymnt.add(array)
                    } else {
                        if (TextUtils.isEmpty(payMents.upi_id) &&
                            (payMents.qr_img.equals(null) || payMents.qr_img.equals(""))) {
                            Toast.makeText(context, "Please enter UPI ID or Mobile Number OR Upload QR.", Toast.LENGTH_SHORT).show()
                            holder.checkBox.isChecked = false
                        } else {
                            array.add(payMents)
                            for (i in 0 until array.size) {
                                Log.d("TAG", "methodid--> " + array.get(i).payment_method_id)
                                Log.d("TAG", "upiid--> " + array.get(i).upi_id)
                                Log.d("TAG", "upiid--> " + array.get(i).qr_img)
                            }

                            arrapaymnt.add(array)
                        }
                    }


                }

                isProgressShow.showProgress(true)
            } else if (!isChecked) {
                val id = paymentArray[pos].paymentMethodId

                Log.e("HElloIDSSS", "Hii   " + array.size + "   " + paymentArray.size)

                for (i in 0 until array.size) {


                    try {
                        Log.e("HElloIDSSS", "IDSSSSS   " + array.get(i).payment_method_id + "   " + id)
                        if (array.get(i).payment_method_id == id) {

                            payMents = Register.payment()
                            payMents.payment_method_id = array.get(i).payment_method_id
                            payMents.upi_id = array.get(i).upi_id
                            payMents.qr_img = array.get(i).qr_img
                            payMents.is_selected = "0"
                            array.removeAt(i)

                            array.add(payMents)
                            arrapaymnt.add(array)
                        }
                    } catch (e: java.lang.Exception) {

                    }


                }

                isProgressShow.showProgress(false)
                Log.d("TAG", "remove--> $array")
            }
            isProgressShow.showProgress(false)
        }

        holder.checkBox.isChecked = paymentArray[pos].isSelected.equals("1")


    }


    class MyHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        val checkBox = itemView.findViewById(R.id.checkBox) as CheckBox
        val txtPaymntName = itemView.findViewById(R.id.txt_paymnt_name) as AppCompatTextView
        val txtNote = itemView.findViewById(R.id.txtnote) as AppCompatTextView
        val btnDownloadQR = itemView.findViewById(R.id.btn_download_qr) as AppCompatTextView
        val btnUploadQR   = itemView.findViewById(R.id.btn_upload_qr) as AppCompatTextView
        val btnEditQR   = itemView.findViewById(R.id.btn_edit_qr) as AppCompatTextView
        val edtUpiId = itemView.findViewById(R.id.edt_upi_id) as AppCompatEditText
        val img_qr_code = itemView.findViewById(R.id.img_edit_qr_code) as AppCompatImageView
        val txtview = itemView.findViewById(R.id.textView) as TextInputLayout
        val view = itemView.findViewById(R.id.view) as View


    }

    interface addQRCode {
        fun qr_codee(img: AppCompatImageView, checks: CheckBox, name: String)
    }

    interface isProgress {
        fun showProgress(show: Boolean)
    }

    interface arrayPayment {
        fun add(array: ArrayList<Register.payment>)
    }


    inner class myTask(val url: String, val holder: MyHolder) : AsyncTask<Void?, Void?, Bitmap?>() {

        override fun doInBackground(vararg params: Void?): Bitmap? {

            try {

                val url = URL(url)
                val `in`: InputStream = url.openConnection().getInputStream()
                val bis = BufferedInputStream(`in`, 1024 * 8)
                val out = ByteArrayOutputStream()
                var len = 0
                val buffer = ByteArray(1024)
                while (bis.read(buffer).also({ len = it }) != -1) {
                    out.write(buffer, 0, len)
                }
                out.close()
                bis.close()
                val data: ByteArray = out.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                Log.e("HElloAsybcd", "bitmap    " + bitmap)
                return bitmap
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            try {
                payMents.qr_img = convert(result!!)
                if (payMents.qr_img.equals(null) || payMents.qr_img.equals("")) {
                    Toast.makeText(context, "Add qr code", Toast.LENGTH_SHORT).show()
                    holder.checkBox.isChecked = false
                } else {
                    array.add(payMents)
                    for (i in array.indices) {
                        Log.d("TAG", "methodid--> " + array.get(i).payment_method_id)
                        Log.d("TAG", "upiid--> " + array.get(i).upi_id)
                        Log.d("TAG", "upiid--> " + array.get(i).qr_img)
                    }

                    arrapaymnt.add(array)
                }


                isProgressShow.showProgress(false)
            } catch (e: Exception) {

            }


        }

    }


}