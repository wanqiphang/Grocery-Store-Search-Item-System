package com.example.fyp_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp_1.bot.BotResponse
import com.example.fyp_1.bot.MessageAdapter
import com.example.fyp_1.bot.MessageClass
import com.example.fyp_1.bot.MessageSender
import com.example.fyp_1.databinding.ActivityChatbotBinding
import okhttp3.Callback
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Chatbot : AppCompatActivity() {

    private lateinit var binding: ActivityChatbotBinding

    private lateinit var messageList:ArrayList<MessageClass>
    private val USER = 0
    private val BOT = 1
    private val IMAGE = 2
    private lateinit var adapter: MessageAdapter

    //get the branchName from intent
    private lateinit var branchName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageList = ArrayList<MessageClass>()
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.messageList.layoutManager = linearLayoutManager
        adapter = MessageAdapter(this,messageList)
        adapter.setHasStableIds(true)
        binding.messageList.adapter = adapter
        binding.sendBtn.setOnClickListener{
            val msg = binding.messageBox.text.toString()
            sendMessage(msg)
            adapter.notifyItemInserted(messageList.size-1)
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
            binding.messageBox.setText("")
        }


        // Retrieve the branch name from the intent
        branchName = intent.getStringExtra("branch_name") ?: ""

        //navigation
        //navigate to Main page
        binding.buttonHome1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //navigate to search
        binding.buttonSearch1.setOnClickListener {
            val intentSearch = Intent(this, SearchItem::class.java).apply {
                putExtra("branch_name", branchName)
            }
            startActivity(intentSearch)
        }
    }

    //chatbot
    fun sendMessage(message:String){
        var userMessage = MessageClass()
        if(message.isEmpty()){
            Toast.makeText(this,"Please type your message",Toast.LENGTH_SHORT).show()
        }
        else{
            userMessage = MessageClass(message,USER)
            messageList.add(userMessage)
            adapter.notifyDataSetChanged()
        }
        val okHttpClient = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://6408-219-92-10-114.ngrok-free.app/webhooks/rest/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val messageSender = retrofit.create(MessageSender::class.java)
        val response = messageSender.messageSender(userMessage)
        response.enqueue(object: retrofit2.Callback<ArrayList<BotResponse>> {
            override fun onResponse(
                call: Call<ArrayList<BotResponse>>,
                response: Response<ArrayList<BotResponse>>
            ) {
                if(response.body() != null || response.body()?.size != 0){
                    if(response.body()!!.size > 1) {
                        for (message in 0..response.body()!!.size-1) {
                            val currentMessage = response.body()!![message]
                            if(currentMessage.image.isNotEmpty()){
                                messageList.add(MessageClass(currentMessage.image,IMAGE))
                            } else if(currentMessage.text.isNotEmpty()) {
                                messageList.add(MessageClass(currentMessage.text, BOT))
                            }
                            adapter.notifyDataSetChanged()
                        }
                    } else{
                        val currentMessage = response.body()!![0]
                        if(currentMessage.text.isNotEmpty()) {
                            messageList.add(MessageClass(currentMessage.text, BOT))
                        }else if (currentMessage.image.isNotEmpty()){
                            messageList.add(MessageClass(currentMessage.image,IMAGE))
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<BotResponse>>, t: Throwable) {
                val message = "Check your connection"
                messageList.add(MessageClass(message,BOT))
            }

        })
    }
}
