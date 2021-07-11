package vanrrtech.app.forumchat

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*


class ChatRecycleViewAdapter (ForumArrayList : ArrayList<UserDataModel.ForumChatContents>, context : Context, activity: Activity) : RecyclerView.Adapter<ChatRecycleViewAdapter.ViewHolder>() {

    var mForumChatArrayList : ArrayList<UserDataModel.ForumChatContents>? = null
    var mContext : Context? = null
    var activity : Activity? = null
//    var myOnClickListner : onClickListner? = null


    init {
        mForumChatArrayList = ForumArrayList
        mContext = context
        this.activity = activity
//        myOnClickListner = onClickListener
    }

    public fun updateForumArrayList (newForumhatList : ArrayList<UserDataModel.ForumChatContents>?){
        mForumChatArrayList = newForumhatList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.chat_child_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // draw to child view
        val chatContent = mForumChatArrayList?.get(position)

        if (chatContent?.chatImageLink.equals("null")){
            holder.chatPicture?.visibility = View.GONE
        } else {
//            loadPictureByPicasso(chatContent!!, holder.chatPicture!!)
            Picasso.with(mContext).load(chatContent?.chatImageLink).into(holder.chatPicture)
            holder.chatPicture?.visibility = View.VISIBLE
        }

        Picasso.with(mContext).load(chatContent?.userPicture).into(holder.profilePicture)
        holder.profilePicture?.visibility = View.VISIBLE

//        Picasso.with(mContext).load(chatContent?.chatImageLink).into(holder.chatPicture)

        holder.chatUserName?.text = chatContent?.chatUserName
        holder.chatMessage?.text = chatContent?.chatMessage
        holder.chatDate?.text = chatContent?.dateChat

        if(chatContent?.chatUserName!!.equals(UserDataModel.mUserInformation?.userName)){
            holder.container?.setBackgroundResource(R.color.dark_chat_purple)
            holder.chatUserName?.setTextColor(mContext?.resources!!.getColor(R.color.white))
            holder.chatMessage?.setTextColor(mContext?.resources!!.getColor(R.color.white))
            holder.chatDate?.setTextColor(mContext?.resources!!.getColor(R.color.white))
        } else {
            holder.container?.setBackgroundResource(R.color.grey_chat)
            holder.chatUserName?.setTextColor(mContext?.resources!!.getColor(R.color.black))
            holder.chatMessage?.setTextColor(mContext?.resources!!.getColor(R.color.black))
            holder.chatDate?.setTextColor(mContext?.resources!!.getColor(R.color.black))
        }

    }

    override fun getItemCount(): Int {
        return  mForumChatArrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var profilePicture : ImageView? = null
        var chatPicture : ImageView? = null
        var chatUserName : TextView? = null
        var chatMessage : TextView? = null
        var chatDate : TextView? = null
        var container : LinearLayout? = null
        var chatCard : CardView? = null
        var parentChat : LinearLayout? = null

        init{
            profilePicture = itemView.findViewById(R.id.chat_profile_picture)
            chatPicture = itemView.findViewById(R.id.chat_image)
            chatUserName = itemView.findViewById(R.id.chat_username)
            chatMessage = itemView.findViewById(R.id.chat_message)
            chatDate = itemView.findViewById(R.id.chat_time)
            container = itemView.findViewById(R.id.chat_container)
            chatCard = itemView.findViewById(R.id.chat_card)
            parentChat = itemView.findViewById(R.id.parent_chat)
        }


    }

    // not used for debugging only
    fun loadPictureByPicasso(chatContent : UserDataModel.ForumChatContents, imageView: ImageView){
        var target : com.squareup.picasso.Target = object : com.squareup.picasso.Target {

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                // loaded bitmap is here (bitmap)
                imageView.setImageBitmap(bitmap)
                imageView.visibility = View.VISIBLE
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

        }
        Picasso.with(mContext).load(chatContent?.chatImageLink).into(target)
    }

}