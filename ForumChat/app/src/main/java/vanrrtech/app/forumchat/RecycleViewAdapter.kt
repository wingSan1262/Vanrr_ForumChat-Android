package vanrrtech.app.forumchat
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.ArrayList


class RecycleViewAdapter (ForumArrayList : ArrayList<UserDataModel.ForumData>, context : Context, onClickListener: onClickListner) : RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>() {

    var mForumArrayList : ArrayList<UserDataModel.ForumData>? = null
    var mContext : Context? = null
    var myOnClickListner : onClickListner? = null


    init {
        mForumArrayList = ForumArrayList
        mContext = context
        myOnClickListner = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.child_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = mForumArrayList?.get(position)

        Picasso.with(mContext).load(userData?.forumPhoto).into(holder.myImageView)
        holder.forumName?.text = userData?.forumName
        holder.creatorName?.text = userData?.creatorUserName
        holder.date?.text = userData?.creationDate
        holder.position = position

        holder.myView?.setOnLongClickListener {
            myOnClickListner?.onItemLongClick(position)
            true
        }

        holder.myView?.setOnClickListener {
            myOnClickListner?.onItemClick(position)
        }

        if(position%2 != 0){
            holder.containerLinearLayout?.setBackgroundColor(Color.parseColor("#F96167"))
        }
    }

    override fun getItemCount(): Int {
        return  mForumArrayList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var myImageView : ImageView? = null
        var forumName : TextView? = null
        var creatorName : TextView? = null
        var date : TextView? = null
        var containerLinearLayout : LinearLayout? = null
        var myView : View? = null
        var position : Int? = null
        init{
            myImageView = itemView.findViewById(R.id.forum_image_view)
            forumName = itemView.findViewById(R.id.forum_name)
            creatorName = itemView.findViewById(R.id.creator)
            date = itemView.findViewById(R.id.date)
            containerLinearLayout = itemView.findViewById(R.id.container_layout)
            myView = itemView
        }


    }

}