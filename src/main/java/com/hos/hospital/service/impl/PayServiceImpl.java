package com.hos.hospital.service.impl;

import com.alipay.api.AlipayApiException;
import com.hos.hospital.bean.AlipayBean;
import com.hos.hospital.config.AlipayUtil;
import com.hos.hospital.service.PayService;
import org.springframework.stereotype.Service;

@Service
public class PayServiceImpl implements PayService {
    @Override
    public String aliPay(AlipayBean alipayBean) throws AlipayApiException {
        return AlipayUtil.connect(alipayBean);
    }
}
