package com.wcp.weathertest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wcp.data.CalendarData;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailsAndDel extends AppCompatActivity {

    private EditText Name;
    private TextView BeginTimeBtn;
    private TextView EndTimeBtn;
    private Switch AllDaySwitch;
    private LinearLayout EndTimeLine;
    private LinearLayout RemindLine;
    private TextView RemindBtn;
    private TextView InviterBtn;
    private TextView ColorBtn;
    private ImageView CalendarColor;
    private Button DeleteCalendar;

    TextView Enter;
    TextView Cancel;
    DatePicker datePick;


    private String selectedName;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int selectedEndYear;
    private int selectedEndMonth;
    private int selectedEndDay;
    private int selectedHour;
    private int selectedMinute;
    private int selectedEndHour;
    private int selectedEndMinute;
    private ArrayList selectedRemind=new ArrayList();
    private String selectInviter;
    private String selectInviterNumber;
    private String selectCalendar;
    private Boolean AllDay=false;

    private Date savedBeginDay;
    private Date savedEndDay;


    private List<CalendarData> old;
    private CalendarData OldD;
    private String id;

    ArrayAdapter<String> adapter;
    List<String> contactsList = new ArrayList<>();

    private Boolean editedBegin=false;
    private Boolean editedEnd=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_details_and_del);

            //ToolBar
            Toolbar bar = (Toolbar) findViewById(R.id.toolbar_edit);
            setSupportActionBar(bar);
            setTitle("编辑事件");
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
        }catch(Exception e){
            e.printStackTrace();
        }


        Name = (EditText) findViewById(R.id.edit_name);
        BeginTimeBtn = (TextView) findViewById(R.id.edit_begintime_btn);
        EndTimeBtn = (TextView) findViewById(R.id.edit_endtime_btn);
        AllDaySwitch = (Switch) findViewById(R.id.edit_allday_switch);
        EndTimeLine = (LinearLayout) findViewById(R.id.edit_endtime_linear);
        RemindLine = (LinearLayout) findViewById(R.id.edit_remind_line);
        RemindBtn = (TextView) findViewById(R.id.edit_remind_btn);
        InviterBtn = (TextView) findViewById(R.id.edit_inviter_btn);
        ColorBtn = (TextView) findViewById(R.id.edit_calendar_belong);
        CalendarColor = (ImageView) findViewById(R.id.edit_calendar_color);
        DeleteCalendar = (Button) findViewById(R.id.delete_btn);


        SimpleDateFormat fm = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat fn = new SimpleDateFormat("HH:mm");
        SimpleDateFormat fall = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

        Intent id_i = getIntent();
        Bundle id_b = id_i.getExtras();
        id = id_b.getString("id");

        old = DataSupport.where("id = ?", id).find(CalendarData.class);
        OldD = old.get(0);

        Log.d("TAG",OldD.toString());

        selectedName = OldD.getName();
        Name.setText(selectedName);

        savedBeginDay = OldD.getDate();
        //BeginTimeBtn.setText(savedBeginDay.getYear()+fall.format(savedBeginDay));
        if(savedBeginDay!=null) {
            BeginTimeBtn.setText(fall.format(savedBeginDay));
        }else{
            savedBeginDay=new Date();
        }

        savedEndDay = OldD.getEndDate();
        //EndTimeBtn.setText(savedEndDay.getYear()+fall.format(savedEndDay));
        if(savedEndDay!=null) {
            EndTimeBtn.setText(fall.format(savedEndDay));
        }else{
            savedEndDay=new Date();
        }

        AllDay = OldD.getAllDay();
        AllDaySwitch.setChecked(AllDay);
        if(AllDay==true){
                EndTimeLine.setVisibility(View.GONE);
        }

        selectInviter = OldD.getInvite();
        InviterBtn.setText(selectInviter);

        selectCalendar = OldD.getBelong();
        ColorBtn.setText(selectCalendar);


        BeginTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomizeDialog(0);
            }
        });
        EndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomizeDialog(1);
            }
        });
        AllDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AllDay = true;
                    EndTimeLine.setVisibility(View.GONE);
                } else {
                    AllDay = false;
                    EndTimeLine.setVisibility(View.VISIBLE);
                }
            }
        });
        RemindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList mSelectItems = new ArrayList();
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsAndDel.this);
                builder.setTitle("添加提醒")
                        .setMultiChoiceItems(R.array.reminder, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    mSelectItems.add(which);
                                } else if (mSelectItems.contains(which)) {
                                    mSelectItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StringBuilder remind_s = new StringBuilder();
                                for (Object i : mSelectItems) {
                                    switch ((int) i) {
                                        case 0: {
                                            mSelectItems.clear();
                                            RemindBtn.setText("无 >");
                                            selectedRemind = mSelectItems;
                                            return;
                                        }
                                        case 1: {
                                            remind_s.append("5m ");
                                            break;
                                        }
                                        case 2: {
                                            remind_s.append("30m ");
                                            break;
                                        }
                                        case 3: {
                                            remind_s.append("1h ");
                                            break;
                                        }
                                        case 4: {
                                            remind_s.append("1d ");
                                            break;
                                        }
                                    }
                                }
                                if (mSelectItems.size() <= 0) {
                                    remind_s.append("无 ");
                                    mSelectItems.add((int) 0);
                                }
                                remind_s.append(">");
                                RemindBtn.setText(remind_s.toString());
                                selectedRemind = mSelectItems;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                builder.show();
            }
        });
        InviterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog invite;
                final AlertDialog.Builder inviteDialog = new AlertDialog.Builder(DetailsAndDel.this);

                inviteDialog.setTitle("邀请");

                try {
                    adapter = new ArrayAdapter<String>(DetailsAndDel.this, android.R.layout.simple_list_item_1, contactsList);
                    //contactsView.setAdapter(adapter);
                    if (ContextCompat.checkSelfPermission(DetailsAndDel.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DetailsAndDel.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                    } else {
                        readContacts();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                inviteDialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectInviter = contactsList.get(which).split("\n")[0];
                        selectInviterNumber = contactsList.get(which).split("\n")[1];
                        InviterBtn.setText(selectInviter + " >");
                    }
                });
                inviteDialog.setNegativeButton("撤销已有邀请", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectInviterNumber = null;
                        selectInviter = null;
                        InviterBtn.setText("无 >");
                    }
                });
                inviteDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                invite = inviteDialog.show();

            }
        });

        ColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder setColorDialog = new AlertDialog.Builder(DetailsAndDel.this);
                setColorDialog.setTitle("选择日历")
                        .setItems(R.array.calendar_color, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: {
                                        ColorBtn.setText("家庭");
                                        CalendarColor.setBackgroundColor(getResources().getColor(R.color.home));
                                        selectCalendar = "home";
                                        break;
                                    }
                                    case 1: {
                                        ColorBtn.setText("工作");
                                        CalendarColor.setBackgroundColor(getResources().getColor(R.color.work));
                                        selectCalendar = "work";
                                        break;
                                    }
                                    case 2: {
                                        ColorBtn.setText("节假日");
                                        CalendarColor.setBackgroundColor(getResources().getColor(R.color.holiday));
                                        selectCalendar = "holiday";
                                        break;
                                    }
                                    case 3: {
                                        ColorBtn.setText("重要事件");
                                        CalendarColor.setBackgroundColor(getResources().getColor(R.color.warn));
                                        selectCalendar = "warn";
                                        break;
                                    }
                                }
                            }
                        });
                setColorDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog setColor = setColorDialog.create();
                setColor.show();
            }
        });

        DeleteCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(DeleteCalendar.getContext());
                builder.setTitle("删除日程")
                        .setMessage("确认要删除该日程？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataSupport.delete(CalendarData.class,Long.valueOf(id));
                                Toast.makeText(DetailsAndDel.this,"已删除",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

    }


    private void readContacts(){
        Cursor cursor=null;
        try{
            cursor=getContentResolver().query(ContactsContract.CommonDataKinds.
                    Phone.CONTENT_URI,null,null,null,null);
            if(cursor!=null){
                while(cursor.moveToNext()){
                    String displayName=cursor.getString(cursor.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number=cursor.getString(cursor.
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactsList.add(displayName+"\n"+number);
                }
                adapter.notifyDataSetChanged();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 1:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
        }
    }


    private void showCustomizeDialog(final int mode) {
    /* @setView 装入自定义View ==> R.layout.dialog_customize
     */
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(DetailsAndDel.this){

                    @Override
                    public AlertDialog create() {

                        return super.create();
                    }

                    @Override
                    public AlertDialog show() {

                        return super.show();
                    }
                };
        final View dialogView = LayoutInflater.from(DetailsAndDel.this)
                .inflate(R.layout.select_date_dialog,null);
        customizeDialog.setTitle("选择日期");
        customizeDialog.setView(dialogView);

        Enter=(TextView)dialogView.findViewById(R.id.select_date_enter);
        Cancel=(TextView)dialogView.findViewById(R.id.select_date_cancel);
        datePick=(DatePicker)dialogView.findViewById(R.id.datePicker);


        /* @setOnDismissListener Dialog销毁时调用
        * @setOnCancelListener Dialog关闭时调用
        */
        customizeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        final AlertDialog selectDialog=customizeDialog.show();

        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editedBegin=true;
                switch(mode){
                    case 0:{
                        selectedYear = datePick.getYear();
                        selectedMonth = datePick.getMonth()+1;
                        selectedDay = datePick.getDayOfMonth();
                        BeginTimeBtn.setText(selectedYear + "年" + selectedMonth + "月" + selectedDay + "日");
                        break;
                    }
                    case 1:{
                        selectedEndYear = datePick.getYear();
                        selectedEndMonth = datePick.getMonth()+1;
                        selectedEndDay = datePick.getDayOfMonth();
                        EndTimeBtn.setText(selectedEndYear + "年" + selectedEndMonth + "月" + selectedEndDay + "日");
                    }
                }
                selectDialog.dismiss();
                if(AllDay==false){
                    showTimeSelectDialog(mode);
                }
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDialog.dismiss();
            }
        });


    }

    private void showTimeSelectDialog(final int mode){
        final AlertDialog.Builder timeSelectDialog =
                new AlertDialog.Builder(DetailsAndDel.this){

                    @Override
                    public AlertDialog create() {

                        return super.create();
                    }

                    @Override
                    public AlertDialog show() {

                        return super.show();
                    }
                };
        final View time_dialogView = LayoutInflater.from(DetailsAndDel.this)
                .inflate(R.layout.select_time_dialog,null);
        timeSelectDialog.setTitle("选择时间");
        timeSelectDialog.setView(time_dialogView);

        final TextView Enter=(TextView)time_dialogView.findViewById(R.id.select_time_enter);
        final TextView Cancel=(TextView)time_dialogView.findViewById(R.id.select_time_cancel);
        final TimePicker timePick=(TimePicker)time_dialogView.findViewById(R.id.timePicker);

        final AlertDialog timeDialog=timeSelectDialog.show();

        Enter.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                editedEnd=true;
                if(mode==0){
                    selectedHour=timePick.getHour();
                    selectedMinute=timePick.getMinute();
                    BeginTimeBtn.setText(BeginTimeBtn.getText()+" "+selectedHour+":"+selectedMinute+" >");
                }
                else if(mode==1){
                    selectedEndHour=timePick.getHour();
                    selectedEndMinute=timePick.getMinute();
                    EndTimeBtn.setText(EndTimeBtn.getText()+" "+selectedEndHour+":"+selectedEndMinute+" >");
                }
                timeDialog.dismiss();
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_back_and_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
            case R.id.save:{
                selectedName=Name.getText().toString();

                if(editedBegin==true) {
                    savedBeginDay.setYear(selectedYear - 1900);
                    savedBeginDay.setMonth(selectedMonth-1);
                    savedBeginDay.setDate(selectedDay);
                    savedBeginDay.setHours(selectedHour);
                    savedBeginDay.setMinutes(selectedMinute);
                }

                if(editedEnd==true) {
                    savedEndDay.setYear(selectedEndYear - 1900);
                    savedEndDay.setMonth(selectedEndMonth-1);
                    savedEndDay.setDate(selectedEndDay);
                    savedEndDay.setHours(selectedEndHour);
                    savedEndDay.setMinutes(selectedEndMinute);
                }
/*
                Date remind=new Date();
                remind.setYear(selectedYear-1900);
                remind.setMonth(selectedMonth-1);
                remind.setHours(selectedHour);
                remind.setMinutes(selectedMinute);
                remind.setDate(selectedDay);
*/
                long remind_l=savedBeginDay.getTime();

                List<Long> rem=new ArrayList<>();
                for(Object i:selectedRemind){
                    switch((int)i){
                        case 1:{
                            Date remind_m=new Date(remind_l-1000*60*5);
                            Log.d("TAG","提醒时间"+remind_m);
                            rem.add(remind_m.getTime());
                            break;
                        }
                        case 2:{
                            Date remind_m=new Date(remind_l-1000*60*30);
                            Log.d("TAG","提醒时间"+remind_m);
                            rem.add(remind_m.getTime());
                            break;
                        }
                        case 3:{
                            Date remind_m=new Date(remind_l-1000*60*60);
                            Log.d("TAG","提醒时间"+remind_m);
                            rem.add(remind_m.getTime());
                            break;
                        }
                        case 4:{
                            Date remind_m=new Date(remind_l-1000*60*60*24);
                            Log.d("TAG","提醒时间"+remind_m);
                            rem.add(remind_m.getTime());
                            break;
                        }
                    }
                }
                if(selectInviter==null){
                    OldD.setToDefault("Invite");
                }
                if(selectedRemind.contains((int)0) ||selectedRemind.size()==0){
                    rem.clear();
                    OldD.setToDefault("Remind");
                }

                try {
                    OldD.setName(selectedName);
                    OldD.setAllDay(AllDay);
                    if(!AllDay) {
                        OldD.setDate(savedBeginDay);
                        OldD.setEndDate(savedEndDay);
                    }else {
                        OldD.setDate(getDayZero(savedBeginDay));
                        OldD.setEndDate(savedBeginDay);
                    }
                    OldD.setRemind(rem);
                    OldD.setInvite(selectInviter);
                    OldD.setBelong(selectCalendar);

                    if(OldD.getBelong()!=null && OldD.getDate()!=null && OldD.getEndDate()!=null) {
                        if(OldD.getDate().after(OldD.getEndDate())) {
                            Toast.makeText(DetailsAndDel.this, "所选时间不合法", Toast.LENGTH_LONG).show();
                        }else{
                            if (OldD.update(Integer.valueOf(id)) == 1) {
                                Toast.makeText(DetailsAndDel.this, "已保存!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DetailsAndDel.this, "save error!", Toast.LENGTH_LONG).show();
                            }

                            Log.d("TAG", OldD.toString());
                            finish();
                        }
                    }else{
                        Toast.makeText(DetailsAndDel.this, "有未填写的项!", Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }


            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Date getDayZero(Date day){
        Date out=day;
        out.setHours(0);
        out.setMinutes(0);
        out.setSeconds(0);

        return out;
    }
}
