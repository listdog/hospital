package com.hos.hospital.service;

import com.alipay.api.AlipayApiException;
import com.hos.hospital.bean.AlipayBean;

public interface PayService {
    String aliPay(AlipayBean alipayBean) throws AlipayApiException;
}
