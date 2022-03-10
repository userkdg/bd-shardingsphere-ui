alter table ec_oms_address_clean_record change address_cipher address varchar(1024) comment '收货人详细地址';
alter table ec_oms_address_modify_record change receiver_name_cipher receiver_name varchar(152) comment '收货人姓名';
alter table ec_oms_address_modify_record change address_cipher address varchar(1024) comment '收货人详细地址';
alter table ec_oms_address_modify_record change receiver_mobile_cipher receiver_mobile varchar(88) comment '收货人手机';
alter table ec_oms_channel_shop_base change shop_password_cipher shop_password varchar(408) comment '店铺密码';
alter table ec_oms_channel_shop_base change shop_phone_number_cipher shop_phone_number varchar(64) comment '店铺绑定手机';
alter table ec_oms_exc_offline_refund_order change account_name_cipher account_name varchar(88) comment '账户名称';
alter table ec_oms_exc_offline_refund_order change account_cipher account varchar(280) comment '账户（支付宝）';
alter table ec_oms_exc_reissue_order change receiver_mobile_cipher receiver_mobile varchar(88) comment '补发收货人手机';
alter table ec_oms_exc_reissue_order change receiver_name_cipher receiver_name varchar(152) comment '补发收货人姓名';
alter table ec_oms_exc_reissue_order change address_cipher address varchar(1024) comment '补发详细地址';
alter table ec_oms_invoice change buyer_address_cipher buyer_address varchar(536) comment '购买方地址';
alter table ec_oms_invoice change invoice_receiver_phone_cipher invoice_receiver_phone varchar(88) comment '收票人手机';
alter table ec_oms_invoice change buyer_bank_account_cipher buyer_bank_account varchar(536) comment '购买方开户行帐号';
alter table ec_oms_invoice change invoice_receiver_address_cipher invoice_receiver_address varchar(1024) comment '收票地址';
alter table ec_oms_invoice change invoice_receiver_cipher invoice_receiver varchar(152) comment '收票人';
alter table ec_oms_order change receiver_mobile_cipher receiver_mobile varchar(88) comment '收货人手机';
alter table ec_oms_order change address_cipher address varchar(1024) comment '收货人详细地址';
alter table ec_oms_order change receiver_name_cipher receiver_name varchar(152) comment '收货人姓名';
alter table ec_oms_order_import change address_cipher address varchar(1024) comment '收货人详细地址';
alter table ec_oms_order_import change receiver_name_cipher receiver_name varchar(152) comment '收货人姓名';
alter table ec_oms_order_import change receiver_mobile_cipher receiver_mobile varchar(88) comment '收货人手机';
alter table ec_oms_plat_address_modify_record change receiver_name_cipher receiver_name varchar(152) comment '收货人姓名';
alter table ec_oms_plat_address_modify_record change receiver_phone_cipher receiver_phone varchar(152) comment '收货人电话';
alter table ec_oms_plat_address_modify_record change address_cipher address varchar(1024) comment '收货人详细地址';
alter table ec_oms_plat_order_decrypt_data change address_decrypted_cipher address_decrypted varchar(1024) comment '地址明文';
alter table ec_oms_plat_order_decrypt_data change receiver_mobile_decrypted_cipher receiver_mobile_decrypted varchar(88) comment '收件人手机明文';
alter table ec_oms_plat_order_decrypt_data change receiver_name_decrypted_cipher receiver_name_decrypted varchar(152) comment '收件人明文';
alter table ec_oms_plat_order_encrypt_data change address_decrypted_cipher address_decrypted varchar(1024) comment '地址明文';
alter table ec_oms_plat_order_encrypt_data change receiver_mobile_decrypted_cipher receiver_mobile_decrypted varchar(88) comment '收件人手机明文';
alter table ec_oms_plat_order_encrypt_data change receiver_name_decrypted_cipher receiver_name_decrypted varchar(152) comment '收件人明文';
alter table ec_oms_plat_tmall_presale_order change receiver_name_cipher receiver_name varchar(152) comment '收货人姓名';
alter table ec_oms_plat_tmall_presale_order change address_cipher address varchar(1024) comment '收货人详细地址';
alter table ec_oms_plat_tmall_presale_order change receiver_mobile_cipher receiver_mobile varchar(152) comment '收货人手机';
alter table ec_oms_self_help_query_log change receiver_mobile_cipher receiver_mobile varchar(88) comment '收货人手机号码';
alter table ec_oms_sms_management change receive_name_cipher receive_name varchar(152) comment '收货人姓名';
alter table ec_oms_sms_management change phone_number_cipher phone_number varchar(88) comment '手机号码';
alter table ec_oms_sms_management_sub change phone_number_cipher phone_number varchar(88) comment '手机号码';
alter table ec_oms_sms_management_sub change receive_name_cipher receive_name varchar(152) comment '收货人姓名';
alter table oms_b2b_client_distri_channel_charge change charge_phone_cipher charge_phone varchar(88) comment '负责人电话';
alter table oms_b2b_client_storehouse change charge_phone_cipher charge_phone varchar(88) comment '负责人电话';
alter table oms_b2b_oper_client_account change account_card_cipher account_card varchar(408) comment '付款/收款账号';
alter table oms_b2b_oper_client_base change client_name_cipher client_name varchar(216) comment '客户姓名';
alter table oms_b2b_oper_client_base change invoice_account_cipher invoice_account varchar(408) comment '开票银行账号';
alter table oms_b2b_oper_client_base change company_address_cipher company_address varchar(1024) comment '公司详细地址';
alter table oms_b2b_oper_client_base change charge_phone_cipher charge_phone varchar(216) comment '负责人电话';
alter table oms_b2b_oper_client_base change receive_invoice_address_cipher receive_invoice_address varchar(1024) comment '收票详细地址';
alter table oms_b2b_oper_client_base change mobile_phone_cipher mobile_phone varchar(512) comment '手机号码';
alter table oms_b2b_oper_client_base change receive_invoice_name_cipher receive_invoice_name varchar(216) comment '收票人姓名';
alter table oms_b2b_oper_client_base change receive_invoice_phone_cipher receive_invoice_phone varchar(216) comment '收票人电话';
alter table sys_user change mobile_cipher mobile varchar(152) comment '手机';
