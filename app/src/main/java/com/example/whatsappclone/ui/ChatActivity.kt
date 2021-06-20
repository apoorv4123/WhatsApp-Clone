package com.example.whatsappclone.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.R
import com.example.whatsappclone.adapters.ChatAdapter
import com.example.whatsappclone.modals.*
import com.example.whatsappclone.utils.isSameDayAs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


const val UID = "uid"
const val NAME = "name"
const val IMAGE = "image"


class ChatActivity : AppCompatActivity() {

    private val friendId by lazy {
        intent.getStringExtra(UID)
    }
    private val name by lazy {
        intent.getStringExtra(NAME)
    }
    private val image by lazy {
        intent.getStringExtra(IMAGE)
    }
    private val mCurrentUid by lazy {
        FirebaseAuth.getInstance().uid!!
    }
    private val db by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser: User
    private val messages = mutableListOf<ChatEvent>()
    lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_chat)

        FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get()
            .addOnSuccessListener {
                currentUser = it.toObject(User::class.java)!!
            }

        // displaying the message
        chatAdapter = ChatAdapter(messages, mCurrentUid)
        msgRv.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }

        nameTv.text = name
        Picasso.get().load(image).into(userImgView)

        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(msgEdtv)
        smileBtn.setOnClickListener {
            emojiPopup.toggle()
        }

        swipeToLoad.setOnRefreshListener {
            val workerScope = CoroutineScope(Dispatchers.Main)
            workerScope.launch {
                delay(2000)
                swipeToLoad.isRefreshing = false
            }
        }

        listenToMessages() { msg, update ->
            if (update) {
                updateMessage(msg)
            } else {
                addMessage(msg)
            }
        }

        sendBtn.setOnClickListener {
            msgEdtv.text?.let {
                if (it.isNotEmpty()) {
                    sendMessage(it.toString())
                    it.clear() // empty the textView
                }
            }
        }

        chatAdapter.highFiveClick = { id, status ->
            updateHighFive(id, status)
        }

        updateReadCount()
//        markAsRead()
    }

    private fun updateReadCount() {
        getInbox(mCurrentUid, friendId!!).child("count").setValue(0)
    }

    private fun updateHighFive(id: String, status: Boolean) {
        getMessages(friendId!!).child(id).updateChildren(mapOf("liked" to status))
    }

    private fun updateMessage(msg: Message) {
        val position = messages.indexOfFirst {
            when (it) {
                is Message -> it.msgId == msg.msgId
                else -> false
            }
        }
        messages[position] = msg

        chatAdapter.notifyItemChanged(position)
    }

    private fun listenToMessages(newMsg: (msg: Message, update: Boolean) -> Unit) {
        getMessages(friendId!!)
            .orderByKey() // childeventListener would listen to all the changes that happened to its children
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val msg = snapshot.getValue(Message::class.java)!!
                    newMsg(msg, false)
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) { // to change the look of the message
                    val msg = snapshot.getValue(Message::class.java)!!
                    newMsg(msg, true)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun addMessage(msg: Message) {
        val eventBefore =
            messages.lastOrNull() // If list is empty, I'll get null or else I'll get the last value

        // Add date header if it's a different day
        if ((eventBefore != null && !eventBefore.sentAt.isSameDayAs(msg.sentAt)) || eventBefore == null) {
            messages.add(
                DateHeader(msg.sentAt, context = this)
            )
        }
        messages.add(msg)
        //Notify adapter of the changes
        chatAdapter.notifyItemInserted(messages.size - 1)
        msgRv.scrollToPosition(messages.size - 1) // scroll to the new message position
    }

    private fun sendMessage(msg: String) {
        val id =
            getMessages(friendId!!).push().key // push() function creates a new id( a unique key) at path -> messages/friendId
        checkNotNull(id) { "Cannot be null" } // This will cause an exception and will stop the working of the app
        val msgMap = Message(msg, mCurrentUid, id)
        getMessages(friendId!!).child(id).setValue(msgMap).addOnSuccessListener {
            // setValue() - This will set the message in realtime database
            Log.i("CHATS", "completed")
        }.addOnFailureListener {
            Log.i("CHATS", it.localizedMessage)
        }
        updateLastMessage(msgMap)
    }

    private fun updateLastMessage(message: Message) {
        val inboxMap = Inbox(message.msg, friendId!!, name!!, image!!, count = 0)
        getInbox(mCurrentUid, friendId!!).setValue(inboxMap).addOnSuccessListener {
            getInbox(friendId!!, mCurrentUid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Inbox::class.java)
                    inboxMap.apply {
                        from = message.senderId
                        name = currentUser.name
                        image = currentUser.thumbImage
                        count = 1
                    }
                    value?.let {
                        if (it.from == message.senderId) {
                            inboxMap.count = value.count + 1
                        }
                    }
                    getInbox(friendId!!, mCurrentUid).setValue(inboxMap)
                }
            })// update friend's inbox. For setting the value first we have to get it. This value is the last value of Inbox model
        }
    }

    private fun markAsRead() {
        getInbox(friendId!!, mCurrentUid).child("count").setValue(0)
    }

    private fun getMessages(friendId: String) = db.reference.child("messages/${getId(friendId)}")

    private fun getInbox(toUser: String, fromUser: String) =
        db.reference.child("chats/$toUser/$fromUser")

    private fun getId(friendId: String): String { // Id for the messages
        return if (friendId > mCurrentUid) {
            mCurrentUid + friendId
        } else {
            friendId + mCurrentUid
        }
    }


    companion object {

        fun createChatActivity(context: Context, id: String, name: String, image: String): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(UID, id)
            intent.putExtra(NAME, name)
            intent.putExtra(IMAGE, image)

            return intent
        }
    }

}