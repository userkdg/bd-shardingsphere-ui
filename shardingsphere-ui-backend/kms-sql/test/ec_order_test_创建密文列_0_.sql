alter table ec_oms_invoice add buyer_address_cipher varchar(536) comment '购买方地址' after buyer_address;
alter table oms_b2b_oper_client_base add client_name_cipher varchar(216) comment '客户姓名' after client_name;
alter table ec_oms_plat_address_modify_record add receiver_name_cipher varchar(152) comment '收货人姓名' after receiver_name;
alter table ec_oms_plat_order_decrypt_data add address_decrypted_cipher varchar(1024) comment '地址明文' after address_decrypted;
alter table ec_oms_sms_management_sub add phone_number_cipher varchar(88) comment '手机号码' after phone_number;
alter table ec_oms_plat_address_modify_record add receiver_phone_cipher varchar(152) comment '收货人电话' after receiver_phone;
alter table ec_oms_plat_tmall_presale_order add receiver_name_cipher varchar(152) comment '收货人姓名' after receiver_name;
alter table oms_b2b_oper_client_base add invoice_account_cipher varchar(408) comment '开票银行账号' after invoice_account;
alter table sys_user add mobile_cipher varchar(152) comment '手机' after mobile;
alter table ec_oms_order_import add address_cipher varchar(1024) comment '收货人详细地址' after address;
alter table ec_oms_address_modify_record add receiver_name_cipher varchar(152) comment '收货人姓名' after receiver_name;
alter table ec_oms_channel_shop_base add shop_password_cipher varchar(408) comment '店铺密码' after shop_password;
alter table ec_oms_invoice add invoice_receiver_phone_cipher varchar(88) comment '收票人手机' after invoice_receiver_phone;
alter table oms_b2b_oper_client_base add company_address_cipher varchar(1024) comment '公司详细地址' after company_address;
alter table oms_b2b_oper_client_base add charge_phone_cipher varchar(216) comment '负责人电话' after charge_phone;
alter table ec_oms_address_modify_record add address_cipher varchar(1024) comment '收货人详细地址' after address;
alter table ec_oms_order_import add receiver_name_cipher varchar(152) comment '收货人姓名' after receiver_name;
alter table oms_b2b_client_storehouse add charge_phone_cipher varchar(88) comment '负责人电话' after charge_phone;
alter table ec_oms_plat_order_encrypt_data add address_decrypted_cipher varchar(1024) comment '地址明文' after address_decrypted;
alter table oms_b2b_oper_client_base add receive_invoice_address_cipher varchar(1024) comment '收票详细地址' after receive_invoice_address;
alter table oms_b2b_oper_client_base add mobile_phone_cipher varchar(512) comment '手机号码' after mobile_phone;
alter table ec_oms_order_import add receiver_mobile_cipher varchar(88) comment '收货人手机' after receiver_mobile;
alter table ec_oms_plat_order_encrypt_data add receiver_mobile_decrypted_cipher varchar(88) comment '收件人手机明文' after receiver_mobile_decrypted;
alter table ec_oms_invoice add buyer_bank_account_cipher varchar(536) comment '购买方开户行帐号' after buyer_bank_account;
alter table oms_b2b_oper_client_base add receive_invoice_name_cipher varchar(216) comment '收票人姓名' after receive_invoice_name;
alter table ec_oms_exc_reissue_order add receiver_mobile_cipher varchar(88) comment '补发收货人手机' after receiver_mobile;
alter table oms_b2b_oper_client_account add account_card_cipher varchar(408) comment '付款/收款账号' after account_card;
alter table ec_oms_exc_offline_refund_order add account_name_cipher varchar(88) comment '账户名称' after account_name;
alter table ec_oms_plat_order_decrypt_data add receiver_mobile_decrypted_cipher varchar(88) comment '收件人手机明文' after receiver_mobile_decrypted;
alter table ec_oms_sms_management add receive_name_cipher varchar(152) comment '收货人姓名' after receive_name;
alter table ec_oms_exc_reissue_order add receiver_name_cipher varchar(152) comment '补发收货人姓名' after receiver_name;
alter table ec_oms_plat_order_encrypt_data add receiver_name_decrypted_cipher varchar(152) comment '收件人明文' after receiver_name_decrypted;
alter table ec_oms_channel_shop_base add shop_phone_number_cipher varchar(64) comment '店铺绑定手机' after shop_phone_number;
alter table ec_oms_plat_address_modify_record add address_cipher varchar(1024) comment '收货人详细地址' after address;
alter table ec_oms_address_clean_record add address_cipher varchar(1024) comment '收货人详细地址' after address;
alter table ec_oms_exc_reissue_order add address_cipher varchar(1024) comment '补发详细地址' after address;
alter table ec_oms_order add receiver_mobile_cipher varchar(88) comment '收货人手机' after receiver_mobile;
alter table ec_oms_plat_tmall_presale_order add address_cipher varchar(1024) comment '收货人详细地址' after address;
alter table ec_oms_exc_offline_refund_order add account_cipher varchar(280) comment '账户（支付宝）' after account;
alter table ec_oms_invoice add invoice_receiver_address_cipher varchar(1024) comment '收票地址' after invoice_receiver_address;
alter table ec_oms_invoice add invoice_receiver_cipher varchar(152) comment '收票人' after invoice_receiver;
alter table ec_oms_order add address_cipher varchar(1024) comment '收货人详细地址' after address;
alter table ec_oms_order add receiver_name_cipher varchar(152) comment '收货人姓名' after receiver_name;
alter table ec_oms_address_modify_record add receiver_mobile_cipher varchar(88) comment '收货人手机' after receiver_mobile;
alter table ec_oms_plat_tmall_presale_order add receiver_mobile_cipher varchar(64) comment '收货人手机' after receiver_mobile;
alter table ec_oms_self_help_query_log add receiver_mobile_cipher varchar(88) comment '收货人手机号码' after receiver_mobile;
alter table oms_b2b_client_distri_channel_charge add charge_phone_cipher varchar(88) comment '负责人电话' after charge_phone;
alter table ec_oms_sms_management_sub add receive_name_cipher varchar(152) comment '收货人姓名' after receive_name;
alter table ec_oms_plat_order_decrypt_data add receiver_name_decrypted_cipher varchar(152) comment '收件人明文' after receiver_name_decrypted;
alter table oms_b2b_oper_client_base add receive_invoice_phone_cipher varchar(216) comment '收票人电话' after receive_invoice_phone;
alter table ec_oms_sms_management add phone_number_cipher varchar(88) comment '手机号码' after phone_number;
