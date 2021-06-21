package com.example.poc2104301453.demo.presentation;

import android.os.Bundle;

import com.example.poc2104301453.demo.R;
import com.example.poc2104301453.pinpadlibrary.managers.PinpadManager;
import com.example.poc2104301453.pinpadlibrary.utilities.DataUtility;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import com.example.poc2104301453.demo.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import static com.example.poc2104301453.pinpadlibrary.ABECS.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_LOGCAT = MainActivity.class.getSimpleName();

    private static final Semaphore sSemaphore = new Semaphore(1, true);

    private void updateContentScrolling(String msg) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                sSemaphore.acquireUninterruptibly();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv_content_scrolling)).setText(msg);

                        sSemaphore.release();
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        binding.fab.setEnabled(false);

        PinpadManager.register();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG_LOGCAT, "onPause");

        finish();

        PinpadManager.unregister();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateContentScrolling(getString(R.string.warning_reading));

        new Thread() {
            @Override
            public void run() {
                try {
                    String contentScrolling = "Triggered at "
                            + Calendar.getInstance().getTime()
                            + "\r\n\r\n";

                    List<Bundle> requestList = new ArrayList<>(0);

                    Bundle input;

                    input = new Bundle();

                    input.putString(CMD_ID, OPN);

                    requestList.add(input);

                    input = new Bundle();

                    input.putString(CMD_ID, GIN);

                    input.putInt   (GIN_ACQIDX, 0);

                    requestList.add(input);

                    input = new Bundle();

                    input.putString(CMD_ID, CLO);

                    requestList.add(input);

                    input = new Bundle();

                    input.putString(CMD_ID, OPN);

                    requestList.add(input);

                    input = new Bundle();

                    input.putString(CMD_ID, CKE);

                    input.putInt   (CKE_KEY,  1);
                    input.putInt   (CKE_MAG,  1);
                    input.putInt   (CKE_ICC,  1);
                    input.putInt   (CKE_CTLS, 1);

                    requestList.add(input);

                    input = new Bundle();

                    input.putString(CMD_ID, TLR);

                    input.putInt   (TLI_ACQIDX, 0);
                    input.putString(TLI_TABVER, "0123456789");

                    List<String> list = new ArrayList<>(0);

                    list.add("3141040107A000000004101000000000000000000001CREDITO         03000200020002076986200000000000000000000000000020D0E8F000F0A00122F850ACF8000400000000FC50ACA00000000000R143B9AC9FF0000000000004E20000009F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3F850ACF8000400000000FC50ACA000");
                    list.add("3141040210A0000000041010D0761300000000000001CREDITO         03000200020002076986200000000000000000000000000020D0E8F000F0A00122F850ACF8000400000000FC50ACA00000000000R143B9AC9FF0000000000004E20000009F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3F850ACF8000400000000FC50ACA000");
                    list.add("3141040310A0000000041010D0761200000000000002DEBITO          03000200020002076986200000000000000000000000000020D0E8F000F0A00122F850ACF8000400000000FC50ACA00000000000R143B9AC9FF0000000000004E20000009F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3F850ACF8000400000000FC50ACA000");
                    list.add("3141040407A000000004306000000000000000000002DEBITO          03000200020002076986200000000000000000000000000020D0E87000F0A00122F850ACF8000400000000FC50ACA00000000000R143B9AC9FF0000000000004E20000009F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3F850ACF8000400000000FC50ACA000");
                    list.add("3141040507A000000003101000000000000000000001CREDITO         03008C008C008C076986200000000000000000000000000020D0E87000F0A00122D84004F8000010000000D84000A80000000000R123B9AC9FF0000000000004E20000009F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3D84004F8000010000000D84000A800");
                    list.add("3141040607A000000003201000000000000000000002DEBITO          03008C008C008C076986200000000000000000000000000020D0E87000F0A00122D84004F8000010000000D84000A80000000000R123B9AC9FF0000000000004E20000009F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3D84004F8000010000000D84000A800");
                    list.add("3141040707A000000494101000000000000000000001ELO CREDITO     03000200020002076986200000000000000000000000000020D0E87000F0F00122FC408480000010000000FC6084900000000000R093B9AC9FF0000000000000001000009F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3FC408480000010000000FC60849000");
                    list.add("3141040807A000000494201000000000000000000002ELO DEBITO      03000200020002076986200000000000000000000000000020D0E87000F0A00122FC408480000010000000FC6084900000000000R093B9AC9FF0000000000000001000019F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3FC408480000010000000FC60849000");
                    list.add("3141040909A000000003050760100000000000000001ELO CREDITO     03000200020002076986200000000000000000000000000020D0E87000F0F00122FC408480000010000000FC6084900000000000R093B9AC9FF0000000000000001000019F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3FC408480000010000000FC60849000");
                    list.add("3141041009A000000003050760200000000000000002ELO DEBITO      03000200020002076986200000000000000000000000000020D0E87000F0F00122FC408480000010000000FC6084900000000000R093B9AC9FF0000000000000001000019F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3FC408480000010000000FC60849000");
                    list.add("3141041109A000000152301010100000000000000001ELO CREDITO     03000200020002076986200000000000000000000000000020D0E87000F0F00122FC408480000010000000FC6084900000000000R093B9AC9FF0000000000000001000019F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3FC408480000010000000FC60849000");
                    list.add("3141041209A000000152301020100000000000000002ELO DEBITO      03000200020002076986200000000000000000000000000020D0E87000F0F00122FC408480000010000000FC6084900000000000R093B9AC9FF0000000000000001000019F02065F2A029A039C0195059F370400000000009F37040000000000000000000000000000000000Y1Z1Y3Z3FC408480000010000000FC60849000");
                    list.add("61120413A00000000404001030000144A6DA428387A502D7DDFB7A74D3F412BE762627197B25435B7A81716A700157DDD06F7CC99D6CA28C2470527E2C03616B9C59217357C2674F583B3BA5C7DCF2838692D023E3562420B4615C439CA97C44DC9A249CFCE7B3BFB22F68228C3AF13329AA4A613CF8DD853502373D62E49AB256D2BC17120E54AEDCED6D96A4287ACC5C04677D4A5A320DB8BEE2F775E5FEC500000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001381A035DA58B482EE2AF75F4C3F2CA469BA4AA6C000000000000000000000000000000000000000000");
                    list.add("61120414A00000000405001030000176B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED5970000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001EBFA0D5D06D8CE702DA3EAE890701D45E274C845000000000000000000000000000000000000000000");
                    list.add("61120415A00000000406001030000248CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747F1F910A1504D5FFB793D94F3B500765E1ABCAD72D9000000000000000000000000000000000000000000");
                    list.add("61120416A00000000307001030000144A89F25A56FA6DA258C8CA8B40427D927B4A1EB4D7EA326BBB12F97DED70AE5E4480FC9C5E8A972177110A1CC318D06D2F8F5C4844AC5FA79A4DC470BB11ED635699C17081B90F1B984F12E92C1C529276D8AF8EC7F28492097D8CD5BECEA16FE4088F6CFAB4A1B42328A1B996F9278B0B7E3311CA5EF856C2F888474B83612A82E4E00D0CD4069A6783140433D50725F00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001B4BC56CC4E88324932CBC643D6898F6FE593B172000000000000000000000000000000000000000000");
                    list.add("61120417A00000000308001030000176D9FD6ED75D51D0E30664BD157023EAA1FFA871E4DA65672B863D255E81E137A51DE4F72BCC9E44ACE12127F87E263D3AF9DD9CF35CA4A7B01E907000BA85D24954C2FCA3074825DDD4C0C8F186CB020F683E02F2DEAD3969133F06F7845166ACEB57CA0FC2603445469811D293BFEFBAFAB57631B3DD91E796BF850A25012F1AE38F05AA5C4D6D03B1DC2E568612785938BBC9B3CD3A910C1DA55A5A9218ACE0F7A21287752682F15832A678D6E1ED0B000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000120D213126955DE205ADC2FD2822BD22DE21CF9A8000000000000000000000000000000000000000000");
                    list.add("61120418A000000003090010300002489D912248DE0A4E39C1A7DDE3F6D2588992C1A4095AFBD1824D1BA74847F2BC4926D2EFD904B4B54954CD189A54C5D1179654F8F9B0D2AB5F0357EB642FEDA95D3912C6576945FAB897E7062CAA44A4AA06B8FE6E3DBA18AF6AE3738E30429EE9BE03427C9D64F695FA8CAB4BFE376853EA34AD1D76BFCAD15908C077FFE6DC5521ECEF5D278A96E26F57359FFAEDA19434B937F1AD999DC5C41EB11935B44C18100E857F431A4A5A6BB65114F174C2D7B59FDF237D6BB1DD0916E644D709DED56481477C75D95CDD68254615F7740EC07F330AC5D67BCD75BF23D28A140826C026DBDE971A37CD3EF9B8DF644AC385010501EFC6509D7A4111FF80A40173F52D7D27E0F26A146A1C8CCB29046000000000000000000000000000000000000000000");
                    list.add("61120419A000000388020010300002488C1CB1A139B95087455684F9AE2AA6FBA31BA504122DB7C194E8BE0B9AB9A9025DBB8FAFEA1AF9EE8A21CB66296611536863D43520407A57F01216CDB313679B47522821FF783C5B0BE31857EBFBA11A737940E3008D521EA019E17B559C93E21224B0B8651E7B6FD622BCC47FE62266261528CF3F130AF948B5C9087D6C6F28EEAAA73F0013EC1394C582A3F540F697443BA1E2F3D03378CB0C9122F7786821C76FF2D6FBFC827164CF609F0B26151F04C6B6A0675B3113225BD4FFB61FE230D2BBB2C335F5BDFCE30ECAA012AF180799FBC62BF8E02A17ABBB6BED510496529000ACF34EEB0F09AF438A9E7EE22FF16746DB2AD04CA7D11B9834C19CB6F71A8D234E47AB09BDB4E6A180E43000000000000000000000000000000000000000000");
                    list.add("61120420A00000053710001030000176C72A09CD16611507AC2AA75F38757A4B50BF319FFC014C7755E4FE1B56ADFC33188C625D6F3B9456B107A2489A966FF501850E6C3C64CD2C7A9F3C0307FB36D880916B49B84AD2FD8D4C0F5199B09C56EF81B590C9D3D23E1B7A45403939CD5D1BBBDE39EDB2581101503AAD4D4ADED1CA531C74AE4825D304919B677AB5DD7C23237CC67538AB1A315B1AE092F24654F8384BA71BC349CC5FFADF576E8AFB84D1C1CBBAE2C9C8D4A08FE8271E282DEF13F21C5721B13ADAE7C4DD9F761BE4A40DB76C2C3000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    list.add("61120421A00000053711001030000176AF17659A353B5E21498A5A7F5F3C23016B64793A20B8401039292D6D032FCA0870121CABA506CE8AD20742A61FD52EF7982DFA5C83B97BB7A0F5896D9DC0803AB05049943809F5B196827498263A98B5355C2582412E3E831732E32B4340395A83F200383D70AF4C58361B780E6DF8D0706E8A450ED1737433D4C39156A6DE18FC82612D888516B3AC8CF1060A14AAC2718D99BBC5425CEE8A32E7DF18754866B284DA602FDBED9D03BEDE06905EDB9B12B23AAEAE0BAC92FE6DC6412530AFED285843511000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    list.add("61120422A00000055799001030000176D05DE82981164BD9B0B3F56368714BDCC5745A5BD3B22A770DBD1303A0ED88EAF83CCB9A15822F6DCF47669CB9F4202E710860019E671533677D076E93F63B6D9238B06234ADC33E04F8BDAE8D50B2020EA0720C0DC68CBF6EDA55C8ED39AC20793E9A40F10DCBF8728FA2E4F2BD03831F0E5E50E2F9F9730BF0D724E5DA9DEB6C3BC3CD31838FD7D282F8FB49559DCB4D5B771DC49EE046E42054C3CB2598773007FC691AEEA7077CC918751340A0ED1254921AD8AB13C633E7C246A9237EAE09E93348B000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    list.add("61120423A00000049401001030000144AAE2752517E82EFD0041A8CA6DA1C993D0961152E4F92E61448492D881D3AF055DF03CB87A4E9B72AF4D29A3FA2403E7D00BDC93CDD5746AD82BC7C4A7C9C1827578D3E391A6BEB5CA36A668BD20DB29360D479FC75CC84FDC78FE9A4C6BF754A26AB0C3153468DC87C8C244F54CC0650214835825BBE7B96985891DFA0804865C9ED562384DED56280B3B8E8176857B17C3F72E081D76E5B9244843B3D012BD9D18405260000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    list.add("61120424A00000049402001030000176BE300EA002D9720E759152B2E843D0FB1F3BA2535B1CA3AA29131571D56AB4676ECEA552C31EA85257B4B6B146D6F9505311EA14B88F4034669A3E674B7C9D2E6D47C62CA65EB7709D287AF9E2E20AB5D0CBD73C56A55990EABFD871D79A3C20BD710C0FB005FABAE6503D5DB19B6747CFDEFDF739E4C217F1959DB97FD8501C7A5BDB71D8FC506BDBABB83ACF33F44F8D8B9CD72F50BE0FBB1B141927BF4C0618F93EC1A48B2583ADF1B01EAD23FD3916D5F471BC24E3C8E94A2FACB98682ADA18C670CA000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    list.add("61120425A00000049403001030000248DAF1A9708C22EBACE93B1067D0DDE5AF1948DA0D9785D7AB7A04B0FBA4089B96A2616C3A7AB3D9A97032EC12218B70576E54506B922DD373E37A47BD5FB72B9D10C5DD450DB6C92454672F20AAA1C8F0AC7B9ABC71D8A1E9D0DB7B4EDD7C01AD3479EAD0A2389B86B95B186A54F6CE1E463701FE675A2CB9639A03FA271812A2552C886D0A7CD9B3D8269AD63E61C98407E914C7FA5B85E952C1F9C3BDF6E1841F3015FD8D76D0463631C8B058D1A765AD6E25210F6BD8ACB70F5A3A262DEC513A07903A86704B6F44F4E28C850AC8F1DB0E29BF57FB68729E67FAAA453B46C484E5490B027965BC0DE6B538F8B0E261BEFD1BA89C06AB5112263AC2014951210F3CCEA263966ACDE1816D327000000000000000000000000000000000000000000");
                    list.add("61120426A0000000250E001030000144AA94A8C6DAD24F9BA56A27C09B01020819568B81A026BE9FD0A3416CA9A71166ED5084ED91CED47DD457DB7E6CBCD53E560BC5DF48ABC380993B6D549F5196CFA77DFB20A0296188E969A2772E8C4141665F8BB2516BA2C7B5FC91F8DA04E8D512EB0F6411516FB86FC021CE7E969DA94D33937909A53A57F907C40C22009DA7532CB3BE509AE173B39AD6A01BA5BB851A7266ABAE64B42A3668851191D49856E17F8FBCD0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    list.add("61120427A0000000250F001030000176C8D5AC27A5E1FB89978C7C6479AF993AB3800EB243996FBB2AE26B67B23AC482C4B746005A51AFA7D2D83E894F591A2357B30F85B85627FF15DA12290F70F05766552BA11AD34B7109FA49DE29DCB0109670875A17EA95549E92347B948AA1F045756DE56B707E3863E59A6CBE99C1272EF65FB66CBB4CFF070F36029DD76218B21242645B51CA752AF37E70BE1A84FF31079DC0048E928883EC4FADD497A719385C2BBBEBC5A66AA5E5655D18034EC51A73472B3AB557493A9BC2179CC8014053B12BAB4000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    list.add("61120428A00000002510001030000248CF98DFEDB3D3727965EE7797723355E0751C81D2D3DF4D18EBAB9FB9D49F38C8C4A826B99DC9DEA3F01043D4BF22AC3550E2962A59639B1332156422F788B9C16D40135EFD1BA94147750575E636B6EBC618734C91C1D1BF3EDC2A46A43901668E0FFC136774080E888044F6A1E65DC9AAA8928DACBEB0DB55EA3514686C6A732CEF55EE27CF877F110652694A0E3484C855D882AE191674E25C296205BBB599455176FDD7BBC549F27BA5FE35336F7E29E68D783973199436633C67EE5A680F05160ED12D1665EC83D1997F10FD05BBDBF9433E8F797AEE3E9F02A34228ACE927ABE62B8B9281AD08D3DF5C7379685045D7BA5FCDE586371C729CF2FD262394ABC4CC173506502446AA9B9FD000000000000000000000000000000000000000000");
                    list.add("61120429A00000055510003010001176C2C70441B08547562C3E311D6DAF742B160F4730BA5FBD304EA4FC6F03432758A16D97F96770690D0688AADB50A6AEF5B8372A91A9E7DC13B4AF1980C865EC879B5B4D8979EE48EA62A629D5E32586915BF174C43871ECD9CFD46EDCDA52BCF94E1B62012219492E0D817D9E39878A6F43DE54ED53E58BC900A4A0D14C419E517F1F0BC267E71C428AD73F9776283A6E31669167D079786CF13274E3F85D866F1885B53375A8D49B2ED96EB271FDB2791BA9FBF1E563C25B858E4B77621BD3F1AC600A2AC000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    list.add("61120430A00000055511003010001248C2F56D084C2C28D96545B3FF747A6A1F1DFF5C0B1D87E4824B876F1B55178D58ED1DD00D22F7324761ADBA006E749F693B9B669D0F8F5E35E1DED8DA0689AA2C29A3E5734EACC7A05240CF668B494DF015D52F2D77065CAA593A71A421D6925DBCD688121C71E10A0E683C9385640B696B0169B7A9D593FEB32170E9B57CB8316F20E96AB674E64FC2B0575546E30C1184D9C2F43260DB8BEF2619B37301A90D106DFA562BA031B5A7370807C0A396A8D136C47BF3C83229F913E71AC7AFB2BBF4527D3C716AE9277CA51DD8E1D56CBDB075CF064C751698CF129791A00FB74E310827D862DE90E8DE4EC8E247325DD3EC7D28299F767C5B125408ABA7E43064F369731C4B8BBC1D4DDD89B39000000000000000000000000000000000000000000");
                    list.add("61120431A00000065101001030000248B0F20A1EB23CAF2FCC53E5B5D05BCE2A9195781B9F225876E8CA237A33335AFC864384EFB152946F6164970CE6C77D93C1C0A58E1FDFBF869CE099FA7492C665205E0121761626C7C8B162BBE564D317712531B9D6F77C1694F1CB4B14C9FA95BDC10D250FD8FCABAB7B8115BD2621AD1B4CEB017F8A142C06F198DC18F3AAD95A19EA9DE9BEC22678F7E303A8D979F545982B12BEF99711429ABCD0A958000132C692DDD1551349558DF67B9CCB002B52EC57A90C01EFBE3A28C8C9FCE61C7087AFF59D11051A3410ED4DF66763564A340339162916ED0296E1C02D4F323F11EAC6CF63CBAD9677D1A304724167C614663988C4D9F79DBF148BE2B1445A042BC63D0FB994F4BB564087C54E2000000000000000000000000000000000000000000");
                    list.add("61120432A00000045914003010001176E5F5BCE3C76E567E5DC9375BC7250D1CD4E0C30070F5DFDDA23AA614649281395D88F43770B8F3529D29C908EEB35A182AB7FA3D9289ACAA7E2569BD23E18B9D61C8B74D1345A86027F18392366B1A2D76C6289D2B4AA424D12DFD28C2E23FFD1847A99C4F4CBAD0D7D64AD6DF9118D8D863F32280CC44A000633AD0AB612251C9CE343582E62BEB2A56947275BAE58741862A6486F7F97279FBA698373EBD4E13F5F7F0EBB35AD3F9736F3C085F55CD1893A15E5F667C08AFBD6C1AF244AF900BF5DB6B7000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
                    list.add("61120433A00000044201003010001176CCFE36E1F7B63F174C52116977C579A93550F8892E45CFF9C453691FA3E8C0319AFC1477FD58A40065557A9C65028D1FFE853CD2F61B91487F488AFCD154639BABC1AB56379C49BE0F777E425C2D73C6149216304D31AA39BB15FBE84C3F171D042328049CED633B1B25A799308CCD16DDBC37DE00AA186B243CA20A8A36EE7FE171A079B51B94CA22FA0AF0EDE8345C4A2C2EC580D351D4A5890DB9673836BB24318ACCCBA96F9ED3339F5FEE05B4291AB47AC32246F0D80590EF78D884A7D68AA3A9FD1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

                    input.putInt   (TLR_NREC, list.size());

                    StringBuilder data = new StringBuilder();

                    for (String item : list) {
                        data.append(item);
                    }

                    input.putString(TLR_DATA, data.toString());

                    requestList.add(input);

                    input = new Bundle();

                    input.putString(CMD_ID, GCR);

                    String date = (new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault()))
                            .format(new Date());

                    input.putInt   (GCR_ACQIDXREQ, 0);
                    input.putInt   (GCR_APPTYPREQ, 99);
                    input.putLong  (GCR_AMOUNT, 0);
                    input.putString(GCR_DATE, date.substring(0, 6));
                    input.putString(GCR_TIME, date.substring(6));
                    input.putString(GCR_TABVER, "2106202112");
                    input.putInt   (GCR_QTDAPP, 0);

                    requestList.add(input);

                    for (Bundle request : requestList) {
                        contentScrolling += "\n";

                        contentScrolling += DataUtility.toJSON(PinpadManager.request(request), true).toString(4);

                        updateContentScrolling(contentScrolling);
                    }
                } catch (Exception exception) {
                    updateContentScrolling(Log.getStackTraceString(exception));
                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
