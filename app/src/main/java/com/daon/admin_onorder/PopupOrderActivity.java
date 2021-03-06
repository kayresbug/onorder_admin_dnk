package com.daon.admin_onorder;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.daon.admin_onorder.model.PrintOrderModel;
import com.google.gson.JsonObject;
import com.sam4s.printer.Sam4sBuilder;
import com.sam4s.printer.Sam4sPrint;

import java.text.DecimalFormat;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopupOrderActivity extends AppCompatActivity {
    TextView title;
    TextView body;
    String str_type;
    String prevAuthNum = "";
    String prevAuthDate = "";
    String vanTr = "";
    String prevCardNo = "";
    String price = "";
    String menu = "";
    AdminApplication app = new AdminApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_popup_order);
        title = findViewById(R.id.popup_title);
        body = findViewById(R.id.pupop_body);
        Intent intent = getIntent();
        String str_title = intent.getStringExtra("title");
        String str_body = intent.getStringExtra("body");
        prevAuthDate = intent.getStringExtra("auth_date");
        prevAuthNum = intent.getStringExtra("auth_num");
        price = intent.getStringExtra("price");
        vanTr = intent.getStringExtra("vantr");
        prevCardNo = intent.getStringExtra("cardbin");
        menu = intent.getStringExtra("menu");


        title.setText(str_title);
        body.setText(str_body);

    }
    public void mOnClose(View v){
        finish();
    }
    public void mOffClose(View v){
        setPayment(price,"cancleNocard");
        finish();
    }

    public void setPayment(String amount, String type) {
        Log.d("daon", "payment = " + amount);

        HashMap<String, byte[]> m_hash = new HashMap<String, byte[]>();
        /*?????? ????????????*/
        m_hash.put("TelegramType", "0200".getBytes());                                    // ?????? ?????? ,  ??????(0200) ??????(0420)
        m_hash.put("DPTID", "AT0292562A".getBytes());                                     // ??????????????? , ????????????????????? DPT0TEST03
        m_hash.put("PosEntry", "S".getBytes());                                           // Pos Entry Mode , ??????????????? ?????? ??? ?????????????????? 'K'??????
        m_hash.put("PayType", "00".getBytes());                                           // [??????]???????????????(default '00') [??????]???????????????
        m_hash.put("TotalAmount", getStrMoneytoTgAmount(amount)); // ?????????
        m_hash.put("Amount", getStrMoneytoTgAmount(amount));      // ???????????? = ????????? - ????????? - ?????????
        m_hash.put("ServicAmount", getStrMoneytoTgAmount("0"));                           // ?????????
        m_hash.put("TaxAmount", getStrMoneytoTgAmount("0"));                              // ?????????
        m_hash.put("FreeAmount", getStrMoneytoTgAmount("0"));                             // ?????? 0??????  / ?????? 1004?????? ?????? ????????? 1004??? ?????????(ServiceAmount),?????????(TaxAmount) 0??? ???????????? 1004???/ ??????(FreeAmount)  1004???
        m_hash.put("AuthNum", "".getBytes());                                            //????????? ???????????? , ??????????????? ??????
        m_hash.put("Authdate", "".getBytes());                                           //????????? ???????????? , ??????????????? ??????
        m_hash.put("Filler", "".getBytes());                                              // ???????????? - ????????? ??????????????? ????????????
        m_hash.put("SignTrans", "N".getBytes());                                          // ???????????? ??????, ?????????(N) 50000??? ????????? ?????? "N" => "S"?????? ??????
        if (Long.parseLong(amount) > 50000)
            m_hash.put("SignTrans", "S".getBytes());                                          // ???????????? ??????, ?????????(N) 50000??? ????????? ?????? "N" => "S"?????? ??????

        m_hash.put("PlayType", "D".getBytes());                                           // ????????????,  ??????????????? ?????????(D)
        m_hash.put("CardType", "".getBytes());                                            // ???????????? ???????????? (?????? ????????????), "" ??????
        m_hash.put("BranchNM", "".getBytes());                                            // ???????????? ,?????? ?????? ?????????????????? ?????? , ????????? "" ??????
        m_hash.put("BIZNO", "".getBytes());                                               // ??????????????? ,KSNET ?????? ????????? ????????????????????? ??????, ?????? ???"" ??????
        m_hash.put("TransType", "".getBytes());                                           // "" ??????
        m_hash.put("AutoClose_Time", "30".getBytes());                                    // ????????? ?????? ?????? ??? ?????? ?????? ex)30??? ??? ??????
        /*?????? ????????????*/
        //m_hash.put("SubBIZNO","".getBytes());                                            // ?????? ??????????????? ,??????????????? ??????????????? ?????? ??? ????????? ??????
        //m_hash.put("Device_PortName","/dev/bus/usb/001/002".getBytes());                 //????????? ?????? ?????? ?????? ??? UsbDevice ??????????????? getDeviceName() ??????????????? , ?????????????????? ????????????
        //m_hash.put("EncryptSign","A!B@C#D4".getBytes());                                 // SignTrans "T"????????? KSCIC?????? ?????? ???????????? ?????? ?????????????????? ????????????, ??????????????????

        ComponentName compName = new ComponentName("ks.kscic_ksr01", "ks.kscic_ksr01.PaymentDlg");

        Intent intent = new Intent(Intent.ACTION_MAIN);

        if (type.equals("credit")) {
            m_hash.put("ReceiptNo", "X".getBytes());  // ??????????????? ????????????, ???????????? ??? "X", ??????????????? ??????????????? "", Key-In????????? "??????????????? ??? ??????" -> Pos Entry Mode 'K;
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        } else if (type.equals("cancle")) {

            //???????????? ?????? ???
            m_hash.put("TelegramType", "0420".getBytes());  // ?????? ?????? ,  ??????(0200) ??????(0420)
            m_hash.put("ReceiptNo", "X".getBytes());        // ??????????????? ????????????, ???????????? ??? "X", ??????????????? ??????????????? "", Key-In????????? "??????????????? ??? ??????" -> Pos Entry Mode 'K;
            m_hash.put("AuthNum", prevAuthNum.getBytes());
            m_hash.put("Authdate", prevAuthDate.getBytes());
        } else if (type.equals("cancleNocard")) {
            //?????? ????????? ?????? ?????????
            m_hash.put("TelegramType", "0420".getBytes()); // ?????? ?????? ,  ??????(0200) ??????(0420)
            m_hash.put("ReceiptNo", "X".getBytes());      // ??????????????? ????????????, ???????????? ??? "X", ??????????????? ??????????????? "", Key-In????????? "??????????????? ??? ??????" -> Pos Entry Mode 'K;
            m_hash.put("VanTr", vanTr.getBytes());        // ?????????????????? , ????????? ????????? ?????? ?????? ??????
            m_hash.put("Cardbin", prevCardNo.getBytes());
            m_hash.put("AuthNum", prevAuthNum.getBytes());
            m_hash.put("Authdate", prevAuthDate.getBytes());
        }
        Sam4sPrint sam4sPrint = new Sam4sPrint();
        isPrinter isPrinter = new isPrinter();
//        try {
//            Log.d("daon_test","print ="+sam4sPrint.getPrinterStatus());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        if(app.IsConnected1()==false)
//        {
//            Sam4sPrint sam4sPrint1 = app.getPrinter();
//            try {
//                sam4sPrint1.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.1.100", 9100);
//                Thread.sleep(300);
//            } catch (Exception exception) {
//                exception.printStackTrace();
//            }
//            app.setPrinter(sam4sPrint1);
//        }

        sam4sPrint = isPrinter.setPrinter1();

        Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
        try {
            // top
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addFeedLine(2);
            builder.addTextBold(true);
            builder.addTextSize(2,1);
            builder.addText("????????????");
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("[?????????]");
            builder.addFeedLine(1);
            builder.addText(prevAuthDate);
            builder.addFeedLine(1);
            builder.addText("?????????????????? ?????????");
            builder.addFeedLine(1);
            builder.addText("????????? \t");
            builder.addText("101-25-66308 \t");
            builder.addText("Tel : 064-796-0517");
            builder.addFeedLine(1);
            builder.addText("????????????????????? ????????? ????????? ????????? 72");
            builder.addFeedLine(1);
            // body
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addText("TID:\t");
            builder.addText("AT0292562A \t");
            builder.addText("A-0000 \t");
            builder.addText("0017");
            builder.addFeedLine(1);
//            builder.addText("????????????: ");
//            builder.addTextSize(2,1);
//            builder.addTextBold(true);
//            builder.addText(prevCardNo);
            builder.addTextSize(1,1);
            builder.addTextBold(false);
            builder.addFeedLine(1);
            builder.addText("????????????: ");
            builder.addText(prevCardNo);
            builder.addFeedLine(1);
            builder.addTextPosition(0);
            builder.addText("????????????: ");
            builder.addText(prevAuthDate);
            builder.addTextPosition(65535);
            builder.addText("(?????????)");
            builder.addFeedLine(1);
            builder.addText("------------------------------------------");
            builder.addFeedLine(2);
            //menu
            DecimalFormat myFormatter = new DecimalFormat("###,###");

            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            // footer
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("IC??????");
            builder.addTextPosition(120);
//                builder.addText("???  ??? : ");
//                //builder.addTextPosition(400);
//                int a = (Integer.parseInt(price))/10;
//                builder.addText(myFormatter.format(a*9)+"???");
//                builder.addFeedLine(1);
//                builder.addText("DDC?????????");
//                builder.addTextPosition(120);
//                builder.addText("????????? : ");
//                builder.addText(myFormatter.format(a*1)+"???");
//                builder.addFeedLine(1);
//                builder.addTextPosition(120);
            builder.addText("???  ??? : ");
            builder.addTextSize(2,1);
            builder.addTextBold(true);
            builder.addText(myFormatter.format(Integer.parseInt(price))+"???");
            builder.addFeedLine(1);
            builder.addTextSize(1,1);
            builder.addTextPosition(120);
            builder.addText("??????No : ");
            builder.addTextBold(true);
            builder.addTextSize(2,1);
            builder.addText(prevAuthNum);
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
//            builder.addText("???????????? : ");
//            builder.addText(prevCardNo);
            builder.addFeedLine(1);
            builder.addText("??????????????? : ");
            builder.addText("AT0292221A");
            builder.addFeedLine(1);
            builder.addText("?????????????????? : ");
            builder.addText(vanTr);
            builder.addFeedLine(1);
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addText("???????????????.");
            builder.addCut(Sam4sBuilder.CUT_FEED);
            sam4sPrint.sendData(builder);
            isPrinter.closePrint1(sam4sPrint);

        } catch (Exception e) {
            e.printStackTrace();
        }
        intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(compName);
        intent.putExtra("AdminInfo_Hash", m_hash);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("daon_test", "onactivity result");
        if (resultCode == RESULT_OK && data != null) {
            HashMap<String, String> m_hash = (HashMap<String, String>) data.getSerializableExtra("result");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (m_hash != null) {
                prevAuthNum = m_hash.get("AuthNum");
                prevAuthDate = m_hash.get("Authdate");

                vanTr = m_hash.get("VanTr");
                prevCardNo = m_hash.get("CardNo");

                //KTC ????????? ??????
                Log.d("payment", "recv [Classification]:: " + (m_hash.get("Classification")));
                System.out.println("recv [TelegramType]:: " + (m_hash.get("TelegramType")));
                System.out.println("recv [Dpt_Id]:: " + (m_hash.get("Dpt_Id")));
                System.out.println("recv [Enterprise_Info]:: " + (m_hash.get("Enterprise_Info")));
                System.out.println("recv [Full_Text_Num]:: " + (m_hash.get("Full_Text_Num")));
                System.out.println("recv [Status]:: " + (m_hash.get("Status")));
                System.out.println("recv [CardType]:: " + (m_hash.get("CardType")));              //'N':???????????? 'G':??????????????? 'C':???????????? 'P'???????????? 'P'????????? ?????????
                System.out.println("recv [Authdate]:: " + (m_hash.get("Authdate")));
                System.out.println("recv [Message1]:: " + (m_hash.get("Message1")));
                System.out.println("recv [Message2]:: " + (m_hash.get("Message2")));
                System.out.println("recv [VanTr]:: " + (m_hash.get("VanTr")));
                System.out.println("recv [AuthNum]:: " + (m_hash.get("AuthNum")));
                System.out.println("recv [FranchiseID]:: " + (m_hash.get("FranchiseID")));
                System.out.println("recv [IssueCode]:: " + (m_hash.get("IssueCode")));
                System.out.println("recv [CardName]:: " + (m_hash.get("CardName")));
                System.out.println("recv [PurchaseCode]:: " + (m_hash.get("PurchaseCode")));
                System.out.println("recv [PurchaseName]:: " + (m_hash.get("PurchaseName")));
                System.out.println("recv [Remain]:: " + (m_hash.get("Remain")));
                System.out.println("recv [point1]:: " + (m_hash.get("point1")));
                System.out.println("recv [point2]:: " + (m_hash.get("point2")));
                System.out.println("recv [point3]:: " + (m_hash.get("point3")));
                System.out.println("recv [notice1]:: " + (m_hash.get("notice1")));
                System.out.println("recv [notice2]:: " + (m_hash.get("notice2")));
                System.out.println("recv [CardNo]:: " + (m_hash.get("CardNo")));
            }else{
                Log.d("daon tet" , "adfadfaf");
            }
            Sam4sPrint sam4sPrint = new Sam4sPrint();
            try {
                Thread.sleep(300);
                sam4sPrint.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.0.38", 9100);
                sam4sPrint.resetPrinter();
                Log.d("daon_test", "printer status = " + sam4sPrint.getPrinterStatus());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
            try {
                // top
                builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
                builder.addFeedLine(2);
                builder.addTextBold(true);
                builder.addTextSize(2,1);
                builder.addText("????????????");
                builder.addFeedLine(1);
                builder.addTextBold(false);
                builder.addTextSize(1,1);
                builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
                builder.addText("[?????????]");
                builder.addFeedLine(1);
                builder.addText(m_hash.get("Authdate"));
                builder.addFeedLine(1);
                builder.addText("?????????????????? ?????????");
                builder.addFeedLine(1);
                builder.addText("????????? \t");
                builder.addText("101-25-66308 \t");
                builder.addText("Tel : 064-796-0517");
                builder.addFeedLine(1);
                builder.addText("????????????????????? ????????? ????????? ????????? 72");
                builder.addFeedLine(1);
                // body
                builder.addText("------------------------------------------");
                builder.addFeedLine(1);
                builder.addText("TID:\t");
                builder.addText("AT0292221A \t");
                builder.addText("A-0000 \t");
                builder.addText("0017");
                builder.addFeedLine(1);
//                builder.addText("????????????: ");
//                builder.addTextSize(2,1);
//                builder.addTextBold(true);
//                builder.addText(m_hash.get("CardName"));
                builder.addTextSize(1,1);
                builder.addTextBold(false);
                builder.addFeedLine(1);
                builder.addText("????????????: ");
                builder.addText(m_hash.get("CardNo"));
                builder.addFeedLine(1);
                builder.addTextPosition(0);
                builder.addText("????????????: ");
                builder.addText(m_hash.get("AuthDate"));
                builder.addTextPosition(65535);
                builder.addText("(?????????)");
                builder.addFeedLine(1);
                builder.addText("------------------------------------------");
                builder.addFeedLine(2);
                //menu
                DecimalFormat myFormatter = new DecimalFormat("###,###");

                builder.addText("------------------------------------------");
                builder.addFeedLine(1);
                // footer
                builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
                builder.addText("IC??????");
                builder.addTextPosition(120);
//                builder.addText("???  ??? : ");
//                //builder.addTextPosition(400);
//                int a = (Integer.parseInt(price))/10;
//                builder.addText(myFormatter.format(a*9)+"???");
//                builder.addFeedLine(1);
//                builder.addText("DDC?????????");
//                builder.addTextPosition(120);
//                builder.addText("????????? : ");
//                builder.addText(myFormatter.format(a*1)+"???");
//                builder.addFeedLine(1);
//                builder.addTextPosition(120);
                builder.addText("???  ??? : ");
                builder.addTextSize(2,1);
                builder.addTextBold(true);
                builder.addText(myFormatter.format(Integer.parseInt(price)+"???"));
                builder.addFeedLine(1);
                builder.addTextSize(1,1);
                builder.addTextPosition(120);
                builder.addText("??????No : ");
                builder.addTextBold(true);
                builder.addTextSize(2,1);
                builder.addText(m_hash.get("AuthNum"));
                builder.addFeedLine(1);
                builder.addTextBold(false);
                builder.addTextSize(1,1);
//                builder.addText("???????????? : ");
//                builder.addText(m_hash.get("PurchaseName"));
//                builder.addFeedLine(1);
                builder.addText("??????????????? : ");
                builder.addText("AT0292221A");
                builder.addFeedLine(1);
                builder.addText("?????????????????? : ");
                builder.addText(vanTr);
                builder.addFeedLine(1);
                builder.addText("------------------------------------------");
                builder.addFeedLine(1);
                builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
                builder.addText("???????????????.");
                builder.addCut(Sam4sBuilder.CUT_FEED);
                sam4sPrint.sendData(builder);
                Thread.sleep(300);
                sam4sPrint.closePrinter();


            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "??????", Toast.LENGTH_LONG).show();

        } else if (resultCode == RESULT_FIRST_USER && data != null) {
            //??????????????????IC ???????????? ?????? ????????? ???????????? ?????? ?????? ??????
            //Toast.makeText(this, "??????????????????IC ?????? ????????? ???????????? ??? ??????????????? ????????????", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(this, "????????? ?????? ??????", Toast.LENGTH_LONG).show();
        }
        // ????????? ????????? ?????? ?????? ??????
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "??? ?????? ??????", Toast.LENGTH_LONG).show();
        }

    }

    public byte[] getStrMoneytoTgAmount(String Money) {
        byte[] TgAmount = null;
        if (Money.length() == 0) {
//            Toast.makeText(MainActivity.this, "????????? ???????????? ????????????", Toast.LENGTH_SHORT).show();
            return "000000001004".getBytes();
        } else {
            Long longMoney = Long.parseLong(Money.replace(",", ""));
            Money = String.format("%012d", longMoney);

            TgAmount = Money.getBytes();
            return TgAmount;
        }
    }
    public void print2(PrintOrderModel printOrderModel){

        Sam4sPrint sam4sPrint = app.getPrinter();
        String[] orderArr = printOrderModel.getOrder().split("###");
        Log.d("daon_test", orderArr[0]);

        String order = printOrderModel.getOrder();
        order = order.replace("###", "");
        order = order.replace("##", "");
        try {
            Log.d("daon_test","print ="+sam4sPrint.getPrinterStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
        try {
            // top
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addFeedLine(2);
            builder.addTextBold(true);
            builder.addTextSize(2,1);
            builder.addText("????????????");
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("[?????????]");
            builder.addFeedLine(1);
            builder.addText(printOrderModel.getTime());
            builder.addFeedLine(1);
            builder.addText("?????????????????? ?????????");
            builder.addFeedLine(1);
            builder.addText("?????? \t");
            builder.addText("101-25-66308 \t");
            builder.addText("Tel : 064-796-0517");
            builder.addFeedLine(1);
            builder.addText("????????????????????? ????????? ????????? ????????? 72");
            builder.addFeedLine(1);
            // body
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addText("TID:\t");
            builder.addText("AT0292221A \t");
            builder.addText("A-0000 \t");
            builder.addText("0017");
            builder.addFeedLine(1);
            builder.addText("????????????: ");
            builder.addTextSize(2,1);
            builder.addTextBold(true);
            builder.addText(printOrderModel.getCardname());
            builder.addTextSize(1,1);
            builder.addTextBold(false);
            builder.addFeedLine(1);
            builder.addText("????????????: ");
            builder.addText(printOrderModel.getCardnum());
            builder.addFeedLine(1);
            builder.addTextPosition(0);
            builder.addText("????????????: ");
            builder.addText(printOrderModel.getAuthdate());
            builder.addTextPosition(65535);
            builder.addText("(?????????)");
            builder.addFeedLine(1);
            builder.addText("------------------------------------------");
            builder.addFeedLine(2);
            //menu
            DecimalFormat myFormatter = new DecimalFormat("###,###");

            for (int i = 0; i < orderArr.length; i++) {
                String arrOrder = orderArr[i];
                String[] subOrder = arrOrder.split("##");
                builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
                builder.addText(subOrder[0]);
                builder.addText(subOrder[1]);
                builder.addFeedLine(1);
                builder.addTextAlign(Sam4sBuilder.ALIGN_RIGHT);
                builder.addText(subOrder[2]);
                builder.addFeedLine(2);
            }
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            // footer
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("IC??????");
            builder.addTextPosition(120);
            builder.addText("???  ??? : ");
            //builder.addTextPosition(400);
            int a = (Integer.parseInt(printOrderModel.getPrice()))/10;
            builder.addText(myFormatter.format(a*9)+"???");
            builder.addFeedLine(1);
            builder.addText("DDC?????????");
            builder.addTextPosition(120);
            builder.addText("????????? : ");
            builder.addText(myFormatter.format(a*1)+"???");
            builder.addFeedLine(1);
            builder.addTextPosition(120);
            builder.addText("???  ??? : ");
            builder.addTextSize(2,1);
            builder.addTextBold(true);
            builder.addText(myFormatter.format(Integer.parseInt(printOrderModel.getPrice()))+"???");
            builder.addFeedLine(1);
            builder.addTextSize(1,1);
            builder.addTextPosition(120);
            builder.addText("??????No : ");
            builder.addTextBold(true);
            builder.addTextSize(2,1);
            builder.addText(printOrderModel.getAuthnum());
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
            builder.addText("???????????? : ");
            builder.addText(printOrderModel.getNotice());
            builder.addFeedLine(1);
            builder.addText("??????????????? : ");
            builder.addText("AT0292221A");
            builder.addFeedLine(1);
            builder.addText("?????????????????? : ");
            builder.addText(printOrderModel.getVantr());
            builder.addFeedLine(1);
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addText("???????????????.");
            builder.addCut(Sam4sBuilder.CUT_FEED);
            sam4sPrint.sendData(builder);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}