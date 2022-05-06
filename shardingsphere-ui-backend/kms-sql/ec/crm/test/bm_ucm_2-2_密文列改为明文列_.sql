alter table bm_ucm_customer_1 change address_cipher address varchar(1024) comment '常用详细地址';
alter table bm_ucm_customer_1 change other_contact_num_cipher other_contact_num varchar(152) comment '客户其他号码';
alter table bm_ucm_customer_1 change mobile_cipher mobile varchar(88) comment '客户手机';
alter table bm_ucm_customer_1 change phone_cipher phone varchar(152) comment '客户固话';
alter table bm_ucm_customer_1 change name_cipher name varchar(152) comment '客户姓名';
alter table bm_ucm_customer_2 change mobile_cipher mobile varchar(88) comment '客户手机';
alter table bm_ucm_customer_2 change address_cipher address varchar(1024) comment '常用详细地址';
alter table bm_ucm_customer_2 change other_contact_num_cipher other_contact_num varchar(152) comment '客户其他号码';
alter table bm_ucm_customer_2 change phone_cipher phone varchar(152) comment '客户固话';
alter table bm_ucm_customer_2 change name_cipher name varchar(152) comment '客户姓名';
alter table bm_ucm_customer_3 change mobile_cipher mobile varchar(88) comment '客户手机';
alter table bm_ucm_customer_3 change phone_cipher phone varchar(152) comment '客户固话';
alter table bm_ucm_customer_3 change address_cipher address varchar(1024) comment '常用详细地址';
alter table bm_ucm_customer_3 change other_contact_num_cipher other_contact_num varchar(152) comment '客户其他号码';
alter table bm_ucm_customer_3 change name_cipher name varchar(152) comment '客户姓名';
alter table bm_ucm_customer_address_1 change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_customer_address_1 change receiver_name_cipher receiver_name varchar(152) comment '收货人姓名';
alter table bm_ucm_customer_address_1 change address_cipher address varchar(1024) comment '详细地址';
alter table bm_ucm_customer_address_2 change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_customer_address_2 change address_cipher address varchar(1024) comment '详细地址';
alter table bm_ucm_customer_address_2 change receiver_name_cipher receiver_name varchar(152) comment '收货人姓名';
alter table bm_ucm_customer_address_3 change address_cipher address varchar(1024) comment '详细地址';
alter table bm_ucm_customer_address_3 change receiver_name_cipher receiver_name varchar(152) comment '收货人姓名';
alter table bm_ucm_customer_address_3 change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_customer_buy_info_1 change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_customer_buy_info_2 change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_customer_buy_info_3 change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_customer_buy_order_info_1 change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_customer_buy_order_info_2 change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_customer_buy_order_info_3 change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_customer_call_info change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_customer_import change mobile_cipher mobile varchar(88) comment '客户手机';
alter table bm_ucm_customer_import change name_cipher name varchar(152) comment '客户姓名';
alter table bm_ucm_customer_import change phone_cipher phone varchar(152) comment '客户固话';
alter table bm_ucm_customer_import change other_contact_num_cipher other_contact_num varchar(152) comment '客户其他号码';
alter table bm_ucm_customer_import change address_cipher address varchar(1024) comment '常用详细地址';
alter table bm_ucm_customer_oper_log_1 change masking_cipher masking varchar(1024) comment '记录内容需要脱敏的内容，多个值之间用逗号隔开';
alter table bm_ucm_customer_oper_log_2 change masking_cipher masking varchar(1024) comment '记录内容需要脱敏的内容，多个值之间用逗号隔开';
alter table bm_ucm_customer_oper_log_3 change masking_cipher masking varchar(1024) comment '记录内容需要脱敏的内容，多个值之间用逗号隔开';
alter table bm_ucm_jd_membership_info change address_cipher address varchar(1024) comment '详细地址';
alter table bm_ucm_jd_membership_info change user_mobile_cipher user_mobile varchar(280) comment '手机';
alter table bm_ucm_member_cy_customer change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_member_wx_user change mobile_cipher mobile varchar(88) comment '电话，可包含区号';
alter table bm_ucm_wxwork_customer change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_wxwork_customer_follow_user change follow_user_remark_mobiles_cipher follow_user_remark_mobiles varchar(536) comment '客服备注手机号，多个逗号分隔';
alter table bm_ucm_wxwork_import_record change mobile_cipher mobile varchar(280) comment '手机号码';
alter table bm_ucm_wxwork_mobile change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_wxwork_mobile_history change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_yz_customer change mobile_cipher mobile varchar(88) comment '手机号';
alter table bm_ucm_yz_customer_import change mobile_cipher mobile varchar(88) comment '手机号';
alter table scrm_crowd_data change mobile_cipher mobile varchar(88) comment '手机号码';
alter table scrm_marketing_activity_user change mobile_cipher mobile varchar(88) comment '手机号码';
alter table scrm_msg_send_record change mobile_cipher mobile varchar(88) comment '手机号码';
alter table scrm_white_list_import change mobile_cipher mobile varchar(88) comment '手机号码';
