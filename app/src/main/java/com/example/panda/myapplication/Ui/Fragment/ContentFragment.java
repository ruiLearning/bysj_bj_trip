package com.zgsy.bj.Ui.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgsy.bj.Data.ImageData;
import com.zgsy.bj.Data.JianDanHtmlParser;
import com.zgsy.bj.R;
import com.zgsy.bj.Tools.AsynImageLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import static java.lang.Integer.valueOf;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private WebView textView;
    private ImageView imageView;
    final String mimeType = "text/html";
    final String encoding = "utf-8";
    Charset charset = Charset.defaultCharset();
    private OnFragmentInteractionListener mListener;
    private static int position;

    public ContentFragment() {
        // Required empty public constructor
    }

    public static ContentFragment newInstance(Bundle bundle) {
        ContentFragment fragment = new ContentFragment();
        //Bundle args = bundle;
        //position= valueOf(bundle.getString("contentPosition")) ;
        if (bundle.getString("position") != null) {
            Log.i(">>contentPosition>>", "" + bundle.getString("position"));
            position = Integer.parseInt(bundle.getString("position"));
            position--;
        }
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.view1, container, false);
        textView = (WebView) contentView.findViewById(R.id.textview_route);
        imageView = (ImageView) contentView.findViewById(R.id.imageview1);
        AsynImageLoader asynImageLoader = new AsynImageLoader();
        Log.i(">>position", position + "");
        asynImageLoader.showImageAsyn(imageView, ImageData.imageUrl.get(position), 0x7f0200e9);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        try {
            Log.i(">>charset", charset.displayName());
            // 启动异步任务去获取并加载网络HTML内容
//            new LoadHtmlTask().execute("http://www.baidu.com");  // 这里替换为实际的网址
           // textView.loadUrl("http://www.baidu.com");
            // 设置WebView的相关配置
            WebSettings webSettings = textView.getSettings();
            webSettings.setJavaScriptEnabled(true);  // 启用JavaScript，根据实际需求决定是否开启

            // 设置WebViewClient，用于拦截页面加载和控制跳转等行为
            textView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    // 始终返回false，阻止页面跳转，让链接只在当前WebView内加载
                    return false;
                }
            });

            // 加载指定的网络链接
            textView.loadUrl(ImageData.getDataUrl(getContext()).get(position));  // 这里替换为实际的网址

            //textView.loadDataWithBaseURL(null, JianDanHtmlParser.getHtml(ImageData.getDataUrl(getContext()).get(position)), "text/html", "utf-8", null);
            Log.i(">>parse",ImageData.getDataUrl(getContext()).get(position)+"");
            //textView.loadData(new String(JianDanHtmlParser.getHtml(ImageData.getDataUrl(getContext()).get(position)).getBytes(charset),encoding), mimeType,
            //encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentView;
    }

    // 定义一个异步任务类来处理网络请求及内容加载
    public class LoadHtmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String urlString = urls[0];
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();


                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                // 对获取到的HTML内容进行处理，使其能在TextView中正确显示
                result = Html.fromHtml(result).toString();
//                textView.setText(result);
            }

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
