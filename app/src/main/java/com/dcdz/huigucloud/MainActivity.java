package com.dcdz.huigucloud;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dcdz.huigucloud.utils.CommonMethod;
import com.hikvision.netsdk.ADDR_QUERY_TYPE;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CHECK_DDNS_RET;
import com.hikvision.netsdk.NET_DVR_QUERY_COUNTRYID_COND;
import com.hikvision.netsdk.NET_DVR_QUERY_COUNTRYID_RET;
import com.hikvision.netsdk.NET_DVR_QUERY_DDNS_COND;
import com.hikvision.netsdk.NET_DVR_QUERY_DDNS_RET;

import org.apache.log4j.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_address_info)
    Button btnAddressInfo;

    protected static Logger log = Logger.getLogger(MainActivity.class);
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        btnAddressInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddressInfo();
            }
        });
    }

    public void getAddressInfo() {
        NET_DVR_QUERY_COUNTRYID_COND struCountryIDCond = new NET_DVR_QUERY_COUNTRYID_COND();
        NET_DVR_QUERY_COUNTRYID_RET struCountryIDRet = new NET_DVR_QUERY_COUNTRYID_RET();
        struCountryIDCond.wCountryID = 248; //248 is for china,other country's ID please see the interface document
        //first you need get the resolve area server address form www.hik-online.com by country ID
        //and then get you dvr/ipc address from the area resolve server
        if (HCNetSDK.getInstance().NET_DVR_GetAddrInfoByServer(ADDR_QUERY_TYPE.QUERYSVR_BY_COUNTRYID, struCountryIDCond, struCountryIDRet)) {
            log.info("QUERYSVR_BY_COUNTRYID succ, resolve:" + CommonMethod.toValidString(new String(struCountryIDRet.szResolveSvrAddr)));
        } else {
            log.error("QUERYSVR_BY_COUNTRYID failed:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
        //follow code show how to get dvr/ipc address from the area resolve server by nickname or serial no.
        NET_DVR_QUERY_DDNS_COND struDDNSCond = new NET_DVR_QUERY_DDNS_COND();
        NET_DVR_QUERY_DDNS_RET struDDNSQueryRet = new NET_DVR_QUERY_DDNS_RET();
        NET_DVR_CHECK_DDNS_RET struDDNSCheckRet = new NET_DVR_CHECK_DDNS_RET();
        if (HCNetSDK.getInstance().NET_DVR_GetAddrInfoByServer(ADDR_QUERY_TYPE.QUERYDEV_BY_NICKNAME_DDNS, struDDNSCond, struDDNSQueryRet)) {
            log.info("QUERYDEV_BY_NICKNAME_DDNS succ,ip:" + CommonMethod.toValidString(new String(struDDNSQueryRet.szDevIP)) + ", SDK port:" + struDDNSQueryRet.wCmdPort + ", http port" + struDDNSQueryRet.wHttpPort);
        } else {
            log.error("QUERYDEV_BY_NICKNAME_DDNS failed:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
        if (HCNetSDK.getInstance().NET_DVR_GetAddrInfoByServer(ADDR_QUERY_TYPE.QUERYDEV_BY_SERIAL_DDNS, struDDNSCond, struDDNSQueryRet)) {
            log.info("QUERYDEV_BY_SERIAL_DDNS succ,ip:" + CommonMethod.toValidString(new String(struDDNSQueryRet.szDevIP)) + ", SDK port:" + struDDNSQueryRet.wCmdPort + ", http port" + struDDNSQueryRet.wHttpPort);
        } else {
            log.error("QUERYDEV_BY_SERIAL_DDNS failed:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
        //if you get the dvr/ipc address failed from the area reolve server,you can check the reason show as follow
        if (HCNetSDK.getInstance().NET_DVR_GetAddrInfoByServer(ADDR_QUERY_TYPE.CHECKDEV_BY_NICKNAME_DDNS, struDDNSCond, struDDNSCheckRet)) {
            log.info("CHECKDEV_BY_NICKNAME_DDNS succ,ip:" + CommonMethod.toValidString(new String(struDDNSCheckRet.struQueryRet.szDevIP)) + ", SDK port:" + struDDNSCheckRet.struQueryRet.wCmdPort + ", http port" + struDDNSCheckRet.struQueryRet.wHttpPort + ",region:" + struDDNSCheckRet.wRegionID + ",status:" + struDDNSCheckRet.byDevStatus);
        } else {
            log.error("CHECKDEV_BY_NICKNAME_DDNS failed:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
        if (HCNetSDK.getInstance().NET_DVR_GetAddrInfoByServer(ADDR_QUERY_TYPE.CHECKDEV_BY_SERIAL_DDNS, struDDNSCond, struDDNSCheckRet)) {
            log.info("CHECKDEV_BY_SERIAL_DDNS succ,ip:" + CommonMethod.toValidString(new String(struDDNSCheckRet.struQueryRet.szDevIP)) + ", SDK port:" + struDDNSCheckRet.struQueryRet.wCmdPort + ", http port" + struDDNSCheckRet.struQueryRet.wHttpPort + ",region:" + struDDNSCheckRet.wRegionID + ",status:" + struDDNSCheckRet.byDevStatus);
        } else {
            log.error("CHECKDEV_BY_SERIAL_DDNS failed:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
    }

    @Override
    protected void onDestroy() {
        bind.unbind();
        super.onDestroy();
    }
}
