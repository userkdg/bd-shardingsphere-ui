alter table bm_ucm_yz_customer change mobile mobile_plain  varchar(20) null  comment '手机号';
alter table scrm_msg_send_record change mobile mobile_plain  varchar(20) not null  comment '手机号码';
alter table bm_ucm_yz_customer_import change mobile mobile_plain  varchar(20) null  comment '手机号';
alter table bm_ucm_member_cy_customer change mobile mobile_plain  varchar(20) null  comment '手机号';
alter table bm_ucm_customer_buy_order_info_1 change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_customer_call_info change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table scrm_marketing_activity_user change mobile mobile_plain  varchar(20) null  comment '手机号码';
alter table bm_ucm_customer_buy_order_info_3 change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table scrm_white_list_import change mobile mobile_plain  varchar(20) null  comment '手机号码';
alter table bm_ucm_customer_buy_order_info_2 change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_customer_address_2 change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_customer_address_2 change receiver_name receiver_name_plain  varchar(32) null  comment '收货人姓名';
alter table bm_ucm_customer_address_2 change address address_plain  varchar(255) null  comment '详细地址';
alter table bm_ucm_customer_address_3 change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_customer_address_3 change receiver_name receiver_name_plain  varchar(32) null  comment '收货人姓名';
alter table bm_ucm_customer_address_3 change address address_plain  varchar(255) null  comment '详细地址';
alter table bm_ucm_customer_buy_info_3 change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_customer_address_1 change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_customer_address_1 change receiver_name receiver_name_plain  varchar(32) null  comment '收货人姓名';
alter table bm_ucm_customer_address_1 change address address_plain  varchar(255) null  comment '详细地址';
alter table bm_ucm_customer_import change name name_plain  varchar(32) null  comment '客户姓名';
alter table bm_ucm_customer_import change mobile mobile_plain  varchar(20) null  comment '客户手机';
alter table bm_ucm_customer_import change phone phone_plain  varchar(32) null  comment '客户固话';
alter table bm_ucm_customer_import change other_contact_num other_contact_num_plain  varchar(32) null  comment '客户其他号码';
alter table bm_ucm_customer_import change address address_plain  varchar(255) null  comment '常用详细地址';
alter table bm_ucm_wxwork_customer change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_customer_buy_info_1 change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_customer_buy_info_2 change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_wxwork_import_record change mobile mobile_plain  varchar(64) null  comment '手机号码';
alter table bm_ucm_customer_oper_log_3 change masking masking_plain  varchar(255) null  comment '记录内容需要脱敏的内容，多个值之间用逗号隔开';
alter table bm_ucm_jd_membership_info change user_mobile user_mobile_plain  varchar(64) null  comment '手机';
alter table bm_ucm_jd_membership_info change address address_plain  varchar(255) null  comment '详细地址';
alter table bm_ucm_wxwork_mobile change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_customer_oper_log_1 change masking masking_plain  varchar(255) null  comment '记录内容需要脱敏的内容，多个值之间用逗号隔开';
alter table scrm_crowd_data change mobile mobile_plain  varchar(20) not null  comment '手机号码';
alter table bm_ucm_customer_oper_log_2 change masking masking_plain  varchar(255) null  comment '记录内容需要脱敏的内容，多个值之间用逗号隔开';
alter table bm_ucm_wxwork_mobile_history change mobile mobile_plain  varchar(20) not null  comment '手机号';
alter table bm_ucm_wxwork_customer_follow_user change follow_user_remark_mobiles follow_user_remark_mobiles_plain  varchar(128) null  comment '客服备注手机号，多个逗号分隔';
alter table bm_ucm_customer_3 change name name_plain  varchar(32) null  comment '客户姓名';
alter table bm_ucm_customer_3 change mobile mobile_plain  varchar(20) null  comment '客户手机';
alter table bm_ucm_customer_3 change phone phone_plain  varchar(32) null  comment '客户固话';
alter table bm_ucm_customer_3 change other_contact_num other_contact_num_plain  varchar(32) null  comment '客户其他号码';
alter table bm_ucm_customer_3 change address address_plain  varchar(255) null  comment '常用详细地址';
alter table bm_ucm_member_wx_user change mobile mobile_plain  varchar(20) null  comment '电话，可包含区号';
alter table bm_ucm_customer_1 change name name_plain  varchar(32) null  comment '客户姓名';
alter table bm_ucm_customer_1 change mobile mobile_plain  varchar(20) null  comment '客户手机';
alter table bm_ucm_customer_1 change phone phone_plain  varchar(32) null  comment '客户固话';
alter table bm_ucm_customer_1 change other_contact_num other_contact_num_plain  varchar(32) null  comment '客户其他号码';
alter table bm_ucm_customer_1 change address address_plain  varchar(255) null  comment '常用详细地址';
alter table bm_ucm_customer_2 change name name_plain  varchar(32) null  comment '客户姓名';
alter table bm_ucm_customer_2 change mobile mobile_plain  varchar(20) null  comment '客户手机';
alter table bm_ucm_customer_2 change phone phone_plain  varchar(32) null  comment '客户固话';
alter table bm_ucm_customer_2 change other_contact_num other_contact_num_plain  varchar(32) null  comment '客户其他号码';
alter table bm_ucm_customer_2 change address address_plain  varchar(255) null  comment '常用详细地址';
