alter table oms_b2b_client_storehouse change charge_phone charge_phone_plain varchar(20) not null  comment '负责人电话';
alter table ec_oms_address_modify_record change address address_plain varchar(255) comment '收货人详细地址';
alter table ec_oms_address_modify_record change receiver_name receiver_name_plain varchar(32) default '' not null  comment '收货人姓名';
alter table ec_oms_address_modify_record change receiver_mobile receiver_mobile_plain varchar(20) comment '收货人手机';
alter table ec_oms_invoice change buyer_bank_account buyer_bank_account_plain varchar(128) comment '购买方开户行帐号';
alter table ec_oms_invoice change buyer_address buyer_address_plain varchar(128) comment '购买方地址';
alter table ec_oms_invoice change invoice_receiver invoice_receiver_plain varchar(32) comment '收票人';
alter table ec_oms_invoice change invoice_receiver_phone invoice_receiver_phone_plain varchar(20) comment '收票人手机';
alter table ec_oms_invoice change invoice_receiver_address invoice_receiver_address_plain varchar(255) comment '收票地址';
alter table ec_oms_sms_management change phone_number phone_number_plain varchar(20) not null  comment '手机号码';
alter table ec_oms_sms_management change receive_name receive_name_plain varchar(32) comment '收货人姓名';
alter table ec_oms_order change address address_plain varchar(255) comment '收货人详细地址';
alter table ec_oms_order change receiver_name receiver_name_plain varchar(32) default '' not null  comment '收货人姓名';
alter table ec_oms_order change receiver_mobile receiver_mobile_plain varchar(20) comment '收货人手机';
alter table oms_b2b_oper_client_account change account_card account_card_plain varchar(100) comment '付款/收款账号';
alter table ec_oms_plat_address_modify_record change address address_plain varchar(255) comment '收货人详细地址';
alter table ec_oms_plat_address_modify_record change receiver_name receiver_name_plain varchar(32) default '' not null  comment '收货人姓名';
alter table ec_oms_plat_address_modify_record change receiver_phone receiver_phone_plain varchar(32) comment '收货人电话';
alter table ec_oms_plat_order_decrypt_data change address_decrypted address_decrypted_plain varchar(255) comment '地址明文';
alter table ec_oms_plat_order_decrypt_data change receiver_name_decrypted receiver_name_decrypted_plain varchar(32) comment '收件人明文';
alter table ec_oms_plat_order_decrypt_data change receiver_mobile_decrypted receiver_mobile_decrypted_plain varchar(20) comment '收件人手机明文';
alter table ec_oms_sms_management_sub change phone_number phone_number_plain varchar(20) not null  comment '手机号码';
alter table ec_oms_sms_management_sub change receive_name receive_name_plain varchar(32) comment '收货人姓名';
alter table ec_oms_plat_tmall_presale_order change address address_plain varchar(255) comment '收货人详细地址';
alter table ec_oms_plat_tmall_presale_order change receiver_name receiver_name_plain varchar(32) default '' not null  comment '收货人姓名';
alter table ec_oms_plat_tmall_presale_order change receiver_mobile receiver_mobile_plain varchar(15) comment '收货人手机';
alter table oms_b2b_oper_client_base change client_name client_name_plain varchar(50) comment '客户姓名';
alter table oms_b2b_oper_client_base change mobile_phone mobile_phone_plain bigint(11) comment '手机号码';
alter table oms_b2b_oper_client_base change company_address company_address_plain varchar(255) comment '公司详细地址';
alter table oms_b2b_oper_client_base change charge_phone charge_phone_plain varchar(50) comment '负责人电话';
alter table oms_b2b_oper_client_base change invoice_account invoice_account_plain varchar(100) comment '开票银行账号';
alter table oms_b2b_oper_client_base change receive_invoice_name receive_invoice_name_plain varchar(50) comment '收票人姓名';
alter table oms_b2b_oper_client_base change receive_invoice_phone receive_invoice_phone_plain varchar(50) comment '收票人电话';
alter table oms_b2b_oper_client_base change receive_invoice_address receive_invoice_address_plain varchar(255) comment '收票详细地址';
alter table sys_user change mobile mobile_plain varchar(36) comment '手机';
alter table ec_oms_self_help_query_log change receiver_mobile receiver_mobile_plain varchar(20) default '' not null  comment '收货人手机号码';
alter table ec_oms_plat_order_encrypt_data change address_decrypted address_decrypted_plain varchar(255) comment '地址明文';
alter table ec_oms_plat_order_encrypt_data change receiver_name_decrypted receiver_name_decrypted_plain varchar(32) comment '收件人明文';
alter table ec_oms_plat_order_encrypt_data change receiver_mobile_decrypted receiver_mobile_decrypted_plain varchar(20) comment '收件人手机明文';
alter table ec_oms_channel_shop_base change shop_password shop_password_plain varchar(100) not null  comment '店铺密码';
alter table ec_oms_channel_shop_base change shop_phone_number shop_phone_number_plain varchar(15) not null  comment '店铺绑定手机';
alter table ec_oms_exc_offline_refund_order change account account_plain varchar(64) comment '账户（支付宝）';
alter table ec_oms_exc_offline_refund_order change account_name account_name_plain varchar(20) comment '账户名称';
alter table oms_b2b_client_distri_channel_charge change charge_phone charge_phone_plain varchar(20) not null  comment '负责人电话';
alter table ec_oms_order_import change receiver_name receiver_name_plain varchar(32) default '' not null  comment '收货人姓名';
alter table ec_oms_order_import change receiver_mobile receiver_mobile_plain varchar(20) comment '收货人手机';
alter table ec_oms_order_import change address address_plain varchar(255) comment '收货人详细地址';
alter table ec_oms_exc_reissue_order change address address_plain varchar(255) not null  comment '补发详细地址';
alter table ec_oms_exc_reissue_order change receiver_name receiver_name_plain varchar(32) not null  comment '补发收货人姓名';
alter table ec_oms_exc_reissue_order change receiver_mobile receiver_mobile_plain varchar(20) not null  comment '补发收货人手机';
alter table ec_oms_address_clean_record change address address_plain varchar(255) not null  comment '收货人详细地址';
