package com.prathik.ecom.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.gson.Gson
import com.prathik.ecom.MainActivity
import com.prathik.ecom.R
import com.prathik.ecom.network.ApiClient
import com.prathik.ecom.network.models.getuser.GetUser
import com.prathik.ecom.utils.PreferenceManager
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class Register : AppCompatActivity() {


    private var mVerificationId: String? = null
    private var mVerificationMobile: String=""

    //firebase auth object
    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

         //initializing objects
        //initializing objects

        if(PreferenceManager.getBoolean(PreferenceManager.SESSION,false)){
            val intent = Intent(this@Register, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        mAuth = FirebaseAuth.getInstance()


        clickGetOtp()


    }

    private fun clickGetOtp(){
      //  verifyBtn.text="Get OTP"
      //  verifyCode.visibility=View.GONE

        loginBtn.setOnClickListener {
            if(mobileNumber.text.trim().toString().length==10){
               // mobileNumber.visibility=View.GONE
              //  verifyCode.visibility=View.VISIBLE
               // verifyCode.requestFocus()
                mVerificationMobile=mobileNumber.text.toString().trim()
                mobileNumber.setText("")
                loginBtn.text="VERIFY OTP"
                mobileNumber.hint="Enter 6 Digit OTP"

                sendVerificationCode(mVerificationMobile)
                clickVerifyBtn()
            }
            else{
                Toast.makeText(this,"Invalid Number",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clickVerifyBtn(){
        loginBtn.setOnClickListener {
            val code = mobileNumber.text.toString().trim { it <= ' ' }
            if (code.isEmpty() || code.length < 6) {
                mobileNumber.error = "Enter valid code"
                mobileNumber.requestFocus()
            }else{
                //verifying the code entered manually
                verifyVerificationCode(code)
            }
        }
    }


    private fun sendVerificationCode(mobile: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91$mobile",
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallbacks
        )
    }


    private val mCallbacks: OnVerificationStateChangedCallbacks = object : OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            //Getting the code sent by SMS
            val code = phoneAuthCredential.smsCode

            Log.d("EcomLog456","code : $code")

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                mobileNumber!!.setText(code)
                //verifying the code
                verifyVerificationCode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@Register, e.message, Toast.LENGTH_LONG).show()

            Log.d("EcomLog456","Error : ${e.message}")
        }

        override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)

            //storing the verification id that is sent to the user
            mVerificationId = s

            Log.d("EcomLog456","code : $mVerificationId")
        }
    }


    private fun verifyVerificationCode(code: String) {
        //creating the credential
        Log.d("verifycode","code: $code")
        Log.d("verifycode","verid: $mVerificationId")
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)

        //signing the user
        signInWithPhoneAuthCredential(credential)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        loadingLayout.visibility=View.VISIBLE

        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
               override fun onComplete(task: Task<AuthResult?>) {
                   loadingLayout.visibility=View.GONE
                    if (task.isSuccessful()) {
                        //verification successful we will start the profile activity

                        authenticateMobile(mVerificationMobile)
                        // check with api


                    } else {

                        //verification unsuccessful.. display an error message
                        var message = "Somthing is wrong, we will fix it soon..."
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code entered..."
                        }
                        Toast.makeText(this@Register,"$message",Toast.LENGTH_LONG).show()
                    }
                }
            })
    }


    private fun authenticateMobile(phoneNumber:String){
        mobileNumber.clearFocus()
        Log.d("EcomLog456","phonenumber: ${phoneNumber}")
        loadingLayout.visibility=View.VISIBLE
        val call: Call<GetUser> = ApiClient.getClient.getUserbyNumber(phoneNumber)
        call.enqueue(object :Callback<GetUser>{

            override fun onFailure(call: Call<GetUser>, t: Throwable) {
                loadingLayout.visibility=View.GONE
                Toast.makeText(this@Register,"error: ${t.message}",Toast.LENGTH_LONG).show()

                Log.d("EcomLog456","apierror: ${t.message}")
            }

            override fun onResponse(call: Call<GetUser>, response: Response<GetUser>) {
                loadingLayout.visibility=View.GONE
                Log.d("EcomLog456","success")
                Log.d("EcomLog456","URL :${response.raw().request().url()}")


                if(response.code()==200){

                    var userDetail: GetUser? = response.body()
                    val gson = Gson()
                    val successResponse: String = gson.toJson(response.body())
                    Log.d("EcomLog456", "successResponse: $successResponse")

                    Log.d("EcomLog456","response code: ${userDetail?.message}")
                    Log.d("EcomLog456","response code: ${userDetail?.status}")
                    Log.d("EcomLog456","id: ${userDetail?.user?.Id}")


                    if(userDetail?.status==200){
                        var userDetail:GetUser = response.body() as GetUser
                        Log.d("EcomLog456","response code messgae : ${userDetail.message}")

                        PreferenceManager.setBoolean(PreferenceManager.SESSION,true)
                        PreferenceManager.setString(PreferenceManager.USER_NAME,userDetail.user?.userName)
                        PreferenceManager.setString(PreferenceManager.PHONE_NUMBER,userDetail.user?.mobileNumber)
                        PreferenceManager.setString(PreferenceManager.USER_ID,userDetail.user?.Id)

                        val intent = Intent(this@Register, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    }else if(userDetail?.status==500){

                        Toast.makeText(this@Register,"Success: ${response.message()}",Toast.LENGTH_LONG).show()
                        mobileRegLayout.visibility=View.GONE
                        userDetailsregLayout.visibility=View.VISIBLE
                        setRegisterBtn()
                    }


                }else {

                    Toast.makeText(this@Register,"Something went wrong !",Toast.LENGTH_LONG).show()

                }

            }

        })

    }


    private fun setRegisterBtn(){

        loginBtn.setText("REGISTER")
        loginBtn.setOnClickListener {

            if(fullName!=null && newPassword!=null){
                createNewUser(fullName.text.toString().trim(),newPassword.text.toString().trim(),mVerificationMobile)
            }else{

                if(fullName==null)
                    fullName.error = "Cannot be null"
                if(newPassword==null)
                    newPassword.error = "Cannot be null"
            }

        }
    }


    private fun createNewUser(fullname:String,password:String,mobilenumber:String){
        loadingLayout.visibility=View.VISIBLE
        val call: Call<GetUser> = ApiClient.getClient.registerNewUser(fullname,mobilenumber,password)
        call.enqueue(object :Callback<GetUser>{
            override fun onFailure(call: Call<GetUser>, t: Throwable) {
                loadingLayout.visibility=View.GONE
                Toast.makeText(this@Register,"failure: ${t.message}",Toast.LENGTH_LONG).show()

                Log.d("EcomLog456","error: ${t.message}")
            }

            override fun onResponse(call: Call<GetUser>, response: Response<GetUser>) {
                loadingLayout.visibility=View.GONE

                Log.d("EcomLog456","URL :${response.raw().request().url()}")

                val gson = Gson()
                val successResponse: String = gson.toJson(response.body())
                Log.d("EcomLog456", "successResponse: $successResponse")

                var userDetail: GetUser? = response.body()

                Log.d("EcomLog456","success")
                Log.d("EcomLog456","response: ${response.body().toString()}")
                Log.d("EcomLog456","response code: ${response.code()}")

                        if(userDetail?.status==200){ // successs
                            Toast.makeText(this@Register,"${userDetail?.message}",Toast.LENGTH_LONG).show()

                            Log.d("EcomLog456","username: ${userDetail.user?.userName}")
                            Log.d("EcomLog456","mobile: ${userDetail.user?.mobileNumber}")
                            Log.d("EcomLog456","password: ${userDetail.user?.password}")
                            Log.d("EcomLog456","id: ${userDetail.user?.Id}")


                            PreferenceManager.setBoolean(PreferenceManager.SESSION,true)
                            PreferenceManager.setBoolean(PreferenceManager.IS_LOGGED_IN,true)
                            PreferenceManager.setString(PreferenceManager.USER_NAME,userDetail.user?.userName)
                            PreferenceManager.setString(PreferenceManager.PHONE_NUMBER,userDetail.user?.mobileNumber)
                            PreferenceManager.setString(PreferenceManager.USER_ID,userDetail.user?.Id)

                            val intent = Intent(this@Register, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                        }
                       else{
                            Toast.makeText(this@Register,"${userDetail?.message}",Toast.LENGTH_LONG).show()
                        }
            }

        })


    }


}
