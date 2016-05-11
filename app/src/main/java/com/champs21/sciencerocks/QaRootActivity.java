package com.champs21.sciencerocks;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.app.ApplicationSingleton;
import com.champs21.sciencerocks.models.Asktheanchor;
import com.champs21.sciencerocks.models.ModelBase;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.utils.AppConstants;
import com.champs21.sciencerocks.utils.AppUtils;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QaRootActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QaListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private CircularProgressView progressView;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private TextView txtMessage;
    private boolean hasNext;
    private int pageNumber = 1;
    private List<Asktheanchor> listItems;
    private AppCompatButton btnAsk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa_root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listItems = new ArrayList<Asktheanchor>();
        listItems.clear();

        initView();
        initApiCall();
        initAction();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void initView(){
        progressView = (CircularProgressView)this.findViewById(R.id.progressView);
        progressView.setVisibility(View.GONE);
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        mSwipyRefreshLayout = (SwipyRefreshLayout)this.findViewById(R.id.swipyrefreshlayout);
        txtMessage = (TextView)this.findViewById(R.id.txtMessage);
        btnAsk = (AppCompatButton)this.findViewById(R.id.btnAsk);

    }

    private void initAction(){

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.TRANSPARENT)
                        .sizeResId(R.dimen.recyclerViewQaList_divider)
                        .color(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(QaRootActivity.this, R.color.topicDescriptionColor) & 0x00ffffff)))
                        .build());*/

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.e("MainActivity", "Refresh triggered at " + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                if(hasNext == true){
                    pageNumber++;
                    initApiCall();
                }
                else{
                    stopLoader();
                }
            }
        });

        btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAskPopup();
            }
        });

    }

    private void initApiCall(){
        if(listItems.size()<=0){
            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();
        }

        final Map<String, String> params = new HashMap<String, String>();
        params.put("page_number", String.valueOf(pageNumber));
        params.put("page_size", "20");


        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_GET_ANCHOR_LIST), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                stopLoader();

                ModelBase mb = ModelBase.getInstance().setResponse(response);
                if(mb.getStatus().getCode() == 200){

                    hasNext = mb.getData().getHasNext();

                    listItems.addAll(mb.getData().getAsktheanchor());

                    if(listItems.size()<=0){
                        txtMessage.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtMessage.setVisibility(View.GONE);
                    }

                    if(adapter==null){
                        adapter = new QaListAdapter(listItems);
                        recyclerView.setAdapter(adapter);
                    }

                    adapter.notifyDataSetChanged();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);
                stopLoader();
            }
        }){

            @Override
            public Map<String, String> getStringUploads() {
                return params;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(this, new MultiPartStack());
        rq.add(mpr);
    }


    public class QaListAdapter extends RecyclerView.Adapter<QaListAdapter.MyViewHolder> {

        private List<Asktheanchor> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtName;
            TextView txtDate;
            TextView txtQuestion;
            TextView txtAnswer;
            CardView cardView;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.txtName = (TextView) itemView.findViewById(R.id.txtName);
                this.txtDate = (TextView) itemView.findViewById(R.id.txtDate);
                this.txtQuestion = (TextView)itemView.findViewById(R.id.txtQuestion);
                this.txtAnswer = (TextView)itemView.findViewById(R.id.txtAnswer);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);
            }
        }

        public QaListAdapter(List<Asktheanchor> data) {
            this.dataSet = data;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_qa_list_layout2, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView txtName = holder.txtName;
            TextView txtDate = holder.txtDate;
            TextView txtQuestion = holder.txtQuestion;
            TextView txtAnswer = holder.txtAnswer;
            CardView cardView = holder.cardView;

            txtName.setText(dataSet.get(listPosition).getName());
            //txtDate.setText(dataSet.get(listPosition).getDate());
            txtDate.setText(AppUtils.getDateString(dataSet.get(listPosition).getDate(), AppUtils.DATE_FORMAT_APP, AppUtils.DATE_FORMAT_SERVER));
            txtQuestion.setText(dataSet.get(listPosition).getQuestion());
            txtAnswer.setText(dataSet.get(listPosition).getAnswer());


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

    }

    private void stopLoader(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide the refresh after 2sec
                if (QaRootActivity.this != null) {
                    QaRootActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mSwipyRefreshLayout != null)
                                mSwipyRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        }, 500);
    }

    private void showAskPopup(){
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_ask_question)
                .customView(R.layout.layout_dialog_ask_question, false)
                .cancelable(true)
                .show();

        View dialogView = dialog.getCustomView();
        final EditText txtName = (EditText) dialogView.findViewById(R.id.txtName);
        final EditText txtQuestion = (EditText) dialogView.findViewById(R.id.txtQuestion);
        AppCompatButton btnSubmit = (AppCompatButton)dialogView.findViewById(R.id.btnSubmit);
        AppCompatButton btnCancel = (AppCompatButton)dialogView.findViewById(R.id.btnCancel);

        if(!TextUtils.isEmpty(ApplicationSingleton.getInstance().getPrefString(AppConstants.GOOGLE_AUTH_DISPLAY_NAME))){
            txtName.setText(ApplicationSingleton.getInstance().getPrefString(AppConstants.GOOGLE_AUTH_DISPLAY_NAME));
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidPopupForm(txtName, txtQuestion)){
                    //call ask api
                    if(dialog!=null){
                        dialog.dismiss();
                    }

                    initApiCallSubmitQuestion(txtName.getText().toString(), txtQuestion.getText().toString());
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

    }

    private boolean isValidPopupForm(EditText txtName, EditText txtQuestion){
        boolean isValid = true;
        if(TextUtils.isEmpty(txtName.getText().toString())){
            isValid = false;
            Toast.makeText(QaRootActivity.this, R.string.dialog_name_empty, Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(txtQuestion.getText().toString())){
            isValid = false;
            Toast.makeText(QaRootActivity.this, R.string.dialog_question_empty, Toast.LENGTH_SHORT).show();
        }


        return isValid;
    }

    private void initApiCallSubmitQuestion(String name, String question){

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content(R.string.msg_please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .show();

        final Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("question", question);

        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_ASK_QUESTION), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(dialog.isShowing() && dialog!=null){
                    dialog.dismiss();
                }
                ModelBase mb = ModelBase.getInstance().setResponse(response);
                if(mb.getStatus().getCode() == 200){
                    Toast.makeText(QaRootActivity.this, R.string.question_submit_success, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(dialog.isShowing() && dialog!=null){
                    dialog.dismiss();
                }
            }
        }){

            @Override
            public Map<String, String> getStringUploads() {
                return params;
            }
        };

        RequestQueue rq= Volley.newRequestQueue(this, new MultiPartStack());
        rq.add(mpr);

    }

}
