package vanrrtech.app.forumchat
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.ArrayList


class ChatRecycleViewAdapter (ForumArrayList : ArrayList<UserDataModel.ForumChatContents>, context : Context) : RecyclerView.Adapter<ChatRecycleViewAdapter.ViewHolder>() {

    var mForumChatArrayList : ArrayList<UserDataModel.ForumChatContents>? = null
    var mContext : Context? = null
//    var myOnClickListner : onClickListner? = null


    init {
        mForumChatArrayList = ForumArrayList
        mContext = context
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
            Picasso.with(mContext).load(chatContent?.chatImageLink).into(holder.chatPicture)
        }

//        Picasso.with(mContext).load(chatContent?.chatImageLink).into(holder.chatPicture)

        holder.chatUserName?.text = chatContent?.chatUserName
        holder.chatMessage?.text = chatContent?.chatMessage
        holder.chatDate?.text = chatContent?.dateChat

        holder.chatUserName?.setTextColor(Color.parseColor("#FFFFFF"))
        holder.chatMessage?.setTextColor(Color.parseColor("#FFFFFF"))
        holder.chatDate?.setTextColor(Color.parseColor("#FFFFFF"))

        if(chatContent?.chatUserName!!.equals(UserDataModel.mUserInformation?.userName)){
            holder.container?.setBackgroundColor(Color.parseColor("#2a43d1"))
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

        init{
            profilePicture = itemView.findViewById(R.id.chat_profile_picture)
            chatPicture = itemView.findViewById(R.id.chat_image)
            chatUserName = itemView.findViewById(R.id.chat_username)
            chatMessage = itemView.findViewById(R.id.chat_message)
            chatDate = itemView.findViewById(R.id.chat_time)
            container = itemView.findViewById(R.id.chat_container)
        }


    }

}