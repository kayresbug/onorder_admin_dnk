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
        /*고정 사용필드*/
        m_hash.put("TelegramType", "0200".getBytes());                                    // 전문 구분 ,  승인(0200) 취소(0420)
        m_hash.put("DPTID", "AT0292562A".getBytes());                                     // 단말기번호 , 테스트단말번호 DPT0TEST03
        m_hash.put("PosEntry", "S".getBytes());                                           // Pos Entry Mode , 현금영수증 거래 시 키인거래에만 'K'사용
        m_hash.put("PayType", "00".getBytes());                                           // [신용]할부개월수(default '00') [현금]거래자구분
        m_hash.put("TotalAmount", getStrMoneytoTgAmount(amount)); // 총금액
        m_hash.put("Amount", getStrMoneytoTgAmount(amount));      // 공급금액 = 총금액 - 부가세 - 봉사료
        m_hash.put("ServicAmount", getStrMoneytoTgAmount("0"));                           // 봉사료
        m_hash.put("TaxAmount", getStrMoneytoTgAmount("0"));                              // 부가세
        m_hash.put("FreeAmount", getStrMoneytoTgAmount("0"));                             // 면세 0처리  / 면세 1004원일 경우 총금액 1004원 봉사료(ServiceAmount),부가세(TaxAmount) 0원 공급금액 1004원/ 면세(FreeAmount)  1004원
        m_hash.put("AuthNum", "".getBytes());                                            //원거래 승인번호 , 취소시에만 사용
        m_hash.put("Authdate", "".getBytes());                                           //원거래 승인일자 , 취소시에만 사용
        m_hash.put("Filler", "".getBytes());                                              // 여유필드 - 판매차 필요시에만 입력처리
        m_hash.put("SignTrans", "N".getBytes());                                          // 서명거래 필드, 무서명(N) 50000원 초과시 서명 "N" => "S"변경 필수
        if (Long.parseLong(amount) > 50000)
            m_hash.put("SignTrans", "S".getBytes());                                          // 서명거래 필드, 무서명(N) 50000원 초과시 서명 "N" => "S"변경 필수

        m_hash.put("PlayType", "D".getBytes());                                           // 실행구분,  데몬사용시 고정값(D)
        m_hash.put("CardType", "".getBytes());                                            // 은련선택 여부필드 (현재 사용안함), "" 고정
        m_hash.put("BranchNM", "".getBytes());                                            // 가맹점명 ,관련 개발 필요가맹점만 입력 , 없을시 "" 고정
        m_hash.put("BIZNO", "".getBytes());                                               // 사업자번호 ,KSNET 서버 정의된 가맹정일경우만 사용, 없을 시"" 고정
        m_hash.put("TransType", "".getBytes());                                           // "" 고정
        m_hash.put("AutoClose_Time", "30".getBytes());                                    // 사용자 동작 없을 시 자동 종료 ex)30초 후 종료
        /*선택 사용필드*/
        //m_hash.put("SubBIZNO","".getBytes());                                            // 하위 사업자번호 ,하위사업자 현금영수증 승인 및 취소시 적용
        //m_hash.put("Device_PortName","/dev/bus/usb/001/002".getBytes());                 //리더기 포트 설정 필요 시 UsbDevice 인스턴스의 getDeviceName() 리턴값입력 , 필요없을경우 생략가능
        //m_hash.put("EncryptSign","A!B@C#D4".getBytes());                                 // SignTrans "T"일경우 KSCIC에서 서명 받지않고 해당 사인데이터로 승인진행, 특정업체사용

        ComponentName compName = new ComponentName("ks.kscic_ksr01", "ks.kscic_ksr01.PaymentDlg");

        Intent intent = new Intent(Intent.ACTION_MAIN);

        if (type.equals("credit")) {
            m_hash.put("ReceiptNo", "X".getBytes());  // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        } else if (type.equals("cancle")) {

            //신용취소 호출 부
            m_hash.put("TelegramType", "0420".getBytes());  // 전문 구분 ,  승인(0200) 취소(0420)
            m_hash.put("ReceiptNo", "X".getBytes());        // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
            m_hash.put("AuthNum", prevAuthNum.getBytes());
            m_hash.put("Authdate", prevAuthDate.getBytes());
        } else if (type.equals("cancleNocard")) {
            //신용 무카드 취소 호출부
            m_hash.put("TelegramType", "0420".getBytes()); // 전문 구분 ,  승인(0200) 취소(0420)
            m_hash.put("ReceiptNo", "X".getBytes());      // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
            m_hash.put("VanTr", vanTr.getBytes());        // 거래고유번호 , 무카드 취소일 경우 필수 필드
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
            builder.addText("신용취소");
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("[고객용]");
            builder.addFeedLine(1);
            builder.addText(prevAuthDate);
            builder.addFeedLine(1);
            builder.addText("돈내코순두부 한림점");
            builder.addFeedLine(1);
            builder.addText("임순옥 \t");
            builder.addText("101-25-66308 \t");
            builder.addText("Tel : 064-796-0517");
            builder.addFeedLine(1);
            builder.addText("제주특별자치도 제주시 한림읍 한상로 72");
            builder.addFeedLine(1);
            // body
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addText("TID:\t");
            builder.addText("AT0292562A \t");
            builder.addText("A-0000 \t");
            builder.addText("0017");
            builder.addFeedLine(1);
//            builder.addText("카드종류: ");
//            builder.addTextSize(2,1);
//            builder.addTextBold(true);
//            builder.addText(prevCardNo);
            builder.addTextSize(1,1);
            builder.addTextBold(false);
            builder.addFeedLine(1);
            builder.addText("카드번호: ");
            builder.addText(prevCardNo);
            builder.addFeedLine(1);
            builder.addTextPosition(0);
            builder.addText("거래일시: ");
            builder.addText(prevAuthDate);
            builder.addTextPosition(65535);
            builder.addText("(일시불)");
            builder.addFeedLine(1);
            builder.addText("------------------------------------------");
            builder.addFeedLine(2);
            //menu
            DecimalFormat myFormatter = new DecimalFormat("###,###");

            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            // footer
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("IC승인");
            builder.addTextPosition(120);
//                builder.addText("금  액 : ");
//                //builder.addTextPosition(400);
//                int a = (Integer.parseInt(price))/10;
//                builder.addText(myFormatter.format(a*9)+"원");
//                builder.addFeedLine(1);
//                builder.addText("DDC매출표");
//                builder.addTextPosition(120);
//                builder.addText("부가세 : ");
//                builder.addText(myFormatter.format(a*1)+"원");
//                builder.addFeedLine(1);
//                builder.addTextPosition(120);
            builder.addText("합  계 : ");
            builder.addTextSize(2,1);
            builder.addTextBold(true);
            builder.addText(myFormatter.format(Integer.parseInt(price))+"원");
            builder.addFeedLine(1);
            builder.addTextSize(1,1);
            builder.addTextPosition(120);
            builder.addText("승인No : ");
            builder.addTextBold(true);
            builder.addTextSize(2,1);
            builder.addText(prevAuthNum);
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
//            builder.addText("매입사명 : ");
//            builder.addText(prevCardNo);
            builder.addFeedLine(1);
            builder.addText("가맹점번호 : ");
            builder.addText("AT0292221A");
            builder.addFeedLine(1);
            builder.addText("거래일련번호 : ");
            builder.addText(vanTr);
            builder.addFeedLine(1);
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addText("감사합니다.");
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

                //KTC 인증용 출력
                Log.d("payment", "recv [Classification]:: " + (m_hash.get("Classification")));
                System.out.println("recv [TelegramType]:: " + (m_hash.get("TelegramType")));
                System.out.println("recv [Dpt_Id]:: " + (m_hash.get("Dpt_Id")));
                System.out.println("recv [Enterprise_Info]:: " + (m_hash.get("Enterprise_Info")));
                System.out.println("recv [Full_Text_Num]:: " + (m_hash.get("Full_Text_Num")));
                System.out.println("recv [Status]:: " + (m_hash.get("Status")));
                System.out.println("recv [CardType]:: " + (m_hash.get("CardType")));              //'N':신용카드 'G':기프트카드 'C':체크카드 'P'선불카드 'P'고운맘 바우처
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
                builder.addText("신용취소");
                builder.addFeedLine(1);
                builder.addTextBold(false);
                builder.addTextSize(1,1);
                builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
                builder.addText("[고객용]");
                builder.addFeedLine(1);
                builder.addText(m_hash.get("Authdate"));
                builder.addFeedLine(1);
                builder.addText("돈내코순두부 한림점");
                builder.addFeedLine(1);
                builder.addText("임순옥 \t");
                builder.addText("101-25-66308 \t");
                builder.addText("Tel : 064-796-0517");
                builder.addFeedLine(1);
                builder.addText("제주특별자치도 제주시 한림읍 한상로 72");
                builder.addFeedLine(1);
                // body
                builder.addText("------------------------------------------");
                builder.addFeedLine(1);
                builder.addText("TID:\t");
                builder.addText("AT0292221A \t");
                builder.addText("A-0000 \t");
                builder.addText("0017");
                builder.addFeedLine(1);
//                builder.addText("카드종류: ");
//                builder.addTextSize(2,1);
//                builder.addTextBold(true);
//                builder.addText(m_hash.get("CardName"));
                builder.addTextSize(1,1);
                builder.addTextBold(false);
                builder.addFeedLine(1);
                builder.addText("카드번호: ");
                builder.addText(m_hash.get("CardNo"));
                builder.addFeedLine(1);
                builder.addTextPosition(0);
                builder.addText("거래일시: ");
                builder.addText(m_hash.get("AuthDate"));
                builder.addTextPosition(65535);
                builder.addText("(일시불)");
                builder.addFeedLine(1);
                builder.addText("------------------------------------------");
                builder.addFeedLine(2);
                //menu
                DecimalFormat myFormatter = new DecimalFormat("###,###");

                builder.addText("------------------------------------------");
                builder.addFeedLine(1);
                // footer
                builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
                builder.addText("IC승인");
                builder.addTextPosition(120);
//                builder.addText("금  액 : ");
//                //builder.addTextPosition(400);
//                int a = (Integer.parseInt(price))/10;
//                builder.addText(myFormatter.format(a*9)+"원");
//                builder.addFeedLine(1);
//                builder.addText("DDC매출표");
//                builder.addTextPosition(120);
//                builder.addText("부가세 : ");
//                builder.addText(myFormatter.format(a*1)+"원");
//                builder.addFeedLine(1);
//                builder.addTextPosition(120);
                builder.addText("합  계 : ");
                builder.addTextSize(2,1);
                builder.addTextBold(true);
                builder.addText(myFormatter.format(Integer.parseInt(price)+"원"));
                builder.addFeedLine(1);
                builder.addTextSize(1,1);
                builder.addTextPosition(120);
                builder.addText("승인No : ");
                builder.addTextBold(true);
                builder.addTextSize(2,1);
                builder.addText(m_hash.get("AuthNum"));
                builder.addFeedLine(1);
                builder.addTextBold(false);
                builder.addTextSize(1,1);
//                builder.addText("매입사명 : ");
//                builder.addText(m_hash.get("PurchaseName"));
//                builder.addFeedLine(1);
                builder.addText("가맹점번호 : ");
                builder.addText("AT0292221A");
                builder.addFeedLine(1);
                builder.addText("거래일련번호 : ");
                builder.addText(vanTr);
                builder.addFeedLine(1);
                builder.addText("------------------------------------------");
                builder.addFeedLine(1);
                builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
                builder.addText("감사합니다.");
                builder.addCut(Sam4sBuilder.CUT_FEED);
                sam4sPrint.sendData(builder);
                Thread.sleep(300);
                sam4sPrint.closePrinter();


            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "성공", Toast.LENGTH_LONG).show();

        } else if (resultCode == RESULT_FIRST_USER && data != null) {
            //케이에스체크IC 초기버전 이후 가맹점 다운로드 없이 승인 가능
            //Toast.makeText(this, "케이에스체크IC 에서 가맹점 다운로드 후 사용하시기 바랍니다", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(this, "응답값 리턴 실패", Toast.LENGTH_LONG).show();
        }
        // 수행을 제대로 하지 못한 경우
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "앱 호출 실패", Toast.LENGTH_LONG).show();
        }

    }

    public byte[] getStrMoneytoTgAmount(String Money) {
        byte[] TgAmount = null;
        if (Money.length() == 0) {
//            Toast.makeText(MainActivity.this, "테스트 금액으로 승인진행", Toast.LENGTH_SHORT).show();
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
            builder.addText("신용매출");
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
            builder.addText("[고객용]");
            builder.addFeedLine(1);
            builder.addText(printOrderModel.getTime());
            builder.addFeedLine(1);
            builder.addText("돈내코순두부 한림점");
            builder.addFeedLine(1);
            builder.addText("임순 \t");
            builder.addText("101-25-66308 \t");
            builder.addText("Tel : 064-796-0517");
            builder.addFeedLine(1);
            builder.addText("제주특별자치도 제주시 한림읍 한상로 72");
            builder.addFeedLine(1);
            // body
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addText("TID:\t");
            builder.addText("AT0292221A \t");
            builder.addText("A-0000 \t");
            builder.addText("0017");
            builder.addFeedLine(1);
            builder.addText("카드종류: ");
            builder.addTextSize(2,1);
            builder.addTextBold(true);
            builder.addText(printOrderModel.getCardname());
            builder.addTextSize(1,1);
            builder.addTextBold(false);
            builder.addFeedLine(1);
            builder.addText("카드번호: ");
            builder.addText(printOrderModel.getCardnum());
            builder.addFeedLine(1);
            builder.addTextPosition(0);
            builder.addText("거래일시: ");
            builder.addText(printOrderModel.getAuthdate());
            builder.addTextPosition(65535);
            builder.addText("(일시불)");
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
            builder.addText("IC승인");
            builder.addTextPosition(120);
            builder.addText("금  액 : ");
            //builder.addTextPosition(400);
            int a = (Integer.parseInt(printOrderModel.getPrice()))/10;
            builder.addText(myFormatter.format(a*9)+"원");
            builder.addFeedLine(1);
            builder.addText("DDC매출표");
            builder.addTextPosition(120);
            builder.addText("부가세 : ");
            builder.addText(myFormatter.format(a*1)+"원");
            builder.addFeedLine(1);
            builder.addTextPosition(120);
            builder.addText("합  계 : ");
            builder.addTextSize(2,1);
            builder.addTextBold(true);
            builder.addText(myFormatter.format(Integer.parseInt(printOrderModel.getPrice()))+"원");
            builder.addFeedLine(1);
            builder.addTextSize(1,1);
            builder.addTextPosition(120);
            builder.addText("승인No : ");
            builder.addTextBold(true);
            builder.addTextSize(2,1);
            builder.addText(printOrderModel.getAuthnum());
            builder.addFeedLine(1);
            builder.addTextBold(false);
            builder.addTextSize(1,1);
            builder.addText("매입사명 : ");
            builder.addText(printOrderModel.getNotice());
            builder.addFeedLine(1);
            builder.addText("가맹점번호 : ");
            builder.addText("AT0292221A");
            builder.addFeedLine(1);
            builder.addText("거래일련번호 : ");
            builder.addText(printOrderModel.getVantr());
            builder.addFeedLine(1);
            builder.addText("------------------------------------------");
            builder.addFeedLine(1);
            builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
            builder.addText("감사합니다.");
            builder.addCut(Sam4sBuilder.CUT_FEED);
            sam4sPrint.sendData(builder);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}