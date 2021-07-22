package vanrrtech.app.forumchat

import android.os.Handler
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class HTTPRESTClient {

    companion object{
        var appHttpRestClient : HTTPRESTClient? = null

        fun getHttpRestClient(): HTTPRESTClient? {
            if (appHttpRestClient == null){
                appHttpRestClient = HTTPRESTClient()
            }

            return appHttpRestClient
        }

    }

    /**
     * Sending Post Request using HTTP as using simple POST param
     */
    fun sendPostRequest(url : URL, stringParam : String,
                        mHandler : Handler, failValue : Int?, successValue : Int?){

        //Opening the URL using HttpURLConnection
        val conn = url.openConnection() as HttpURLConnection

        conn.requestMethod = "POST"
        val os: OutputStream = conn.getOutputStream()
        val writer = BufferedWriter(
            OutputStreamWriter(os, "UTF-8")
        )
        writer.write(stringParam)

        writer.flush()
        writer.close()
        os.close()
        conn.connect()

        //StringBuilder object to read the string from the service
        val sb = StringBuilder()

        BufferedReader(
            InputStreamReader(conn.inputStream, "utf-8")
        ).use { br ->
            var responseLine: String? = null
            while (br.readLine().also { responseLine = it } != null) {
                sb.append(responseLine!!.trim { it <= ' ' })
            }
        }

        if(failValue != null && sb.toString().contains("fail", true) ){
            mHandler.sendEmptyMessageDelayed(failValue, 500)
        } else {
            val message = mHandler.obtainMessage(successValue!!, sb.toString())
            mHandler.sendMessage(message)
        }

    }

    fun sendPostRequestUsingJsonForm(url : URL, jsonInputString : String,
                                     mHandler : Handler, failValue : Int?, successValue : Int?){
        //Opening the URL using HttpURLConnection
        val conn = url.openConnection() as HttpURLConnection

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true)
        conn.doInput = true

        conn.getOutputStream().use { os ->
            val input: ByteArray = jsonInputString.toByteArray(StandardCharsets.UTF_8)
            os.write(input, 0, input.size)
        }

        //StringBuilder object to read the string from the service
        val sb = StringBuilder()

        BufferedReader(
            InputStreamReader(conn.getInputStream(), "utf-8")
        ).use { br ->
            var responseLine: String? = null
            while (br.readLine().also { responseLine = it } != null) {
                sb.append(responseLine!!.trim { it <= ' ' })
            }
        }

        // TODO
        if(sb.toString().contains("fail", true) && failValue != null){
            mHandler.sendEmptyMessageDelayed(failValue, 500)
        } else {
            val message = mHandler.obtainMessage(successValue!!, sb.toString())
            if (message != null) {
                mHandler.sendMessage(message)
            }
        }
    }

}