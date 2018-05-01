package com.example.onpus.weddingpanda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.onpus.weddingpanda.constant.Budget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskListActivity extends AppCompatActivity {
    @BindView(R.id.gettask)
    EditText gettask;
    DatePicker simpleDatePicker;
    String key = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        ButterKnife.bind(this);
         key = getIntent().getExtras().getString("TitleKey");

         simpleDatePicker = (DatePicker)findViewById(R.id.simpleDatePicker); // initiate a date picker
        simpleDatePicker.setSpinnersShown(false); // set false value for the spinner shown function


    }
    @OnClick(R.id.submitTask)
    public void submit(){
        int day = simpleDatePicker.getDayOfMonth(); // get the selected day of the month
        int month = simpleDatePicker.getMonth(); // get the selected month
        int year = simpleDatePicker.getYear(); // get the selected year
        String date = day+"/"+month+"/"+year+"";
        newItemTask(date,key);
        new SweetAlertDialog(TaskListActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success!")
                .setContentText("You added the item!")
                .show();
    }

    public void newItemTask(String date,String titleid){
        //add album
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference newItem=ref.child("Tasks").child(currentUser.getUid()).child(titleid).child("Items").push();
        final String[] pushkey = {""};

//        com.google.firebase.database.Query mQueryTask = ref.orderByChild("Budgets/"+currentUser.getUid()+"/").equalTo(true);;

        pushkey[0] = newItem.getKey();

        //add item to database
        newItem.setValue(new newTaskItem(gettask.getText().toString(),
                date,   pushkey[0]));

        gettask.setText("");

    }


    public static class newTaskItem{
        String itemTitle,date,itemid;


        public newTaskItem(String itemTitle, String date, String itemid) {
            this.itemTitle = itemTitle;
            this.date = date;
            this.itemid = itemid;
        }

        public newTaskItem() {

        }

        public String getItemTitle() {
            return itemTitle;
        }

        public void setItemTitle(String itemTitle) {
            this.itemTitle = itemTitle;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getItemid() {
            return itemid;
        }

        public void setItemid(String itemid) {
            this.itemid = itemid;
        }
    }
    public static class newTask{
        String tasktitle,date,taskid;

        public newTask(String taskid, String tasktitle) {
            this.tasktitle = tasktitle;
//            this.date = date;
            this.taskid = taskid;
        }

        public String getTasktitle() {
            return tasktitle;
        }

        public void setTasktitle(String tasktitle) {
            this.tasktitle = tasktitle;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTaskid() {
            return taskid;
        }

        public void setTaskid(String taskid) {
            this.taskid = taskid;
        }
    }
}
