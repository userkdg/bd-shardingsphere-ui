alter table mall_wash_appointment_order change address_cipher address varchar(1048) comment '详细地址';
alter table mall_wash_appointment_order change customer_name_cipher customer_name varchar(152) comment '收货人';
alter table mall_wash_appointment_order change customer_phone_cipher customer_phone varchar(152) comment '收货人电话';
alter table mall_wash_back_order change customer_name_cipher customer_name varchar(152) comment '收货人';
alter table mall_wash_back_order change address_cipher address varchar(408) comment '详细地址';
alter table mall_wash_back_order change customer_phone_cipher customer_phone varchar(152) comment '收货人电话';
alter table mall_wash_back_order_log change receive_name_cipher receive_name varchar(152) comment '接收人姓名';
alter table mall_wash_back_order_log change phone_cipher phone varchar(512) comment '接收人手机号';
alter table mall_wash_back_order_operation_log change old_address_cipher old_address varchar(280) comment '旧还衣详细地址';
alter table mall_wash_carriage change finished_name_cipher finished_name varchar(152) comment '完成签收人名称（最后收货人签收名称）';
alter table mall_wash_carriage_address change address_cipher address varchar(1024) comment '详细地址';
alter table mall_wash_clothes_unique_id change customer_phone_cipher customer_phone varchar(64) comment '消费者手机号';
alter table mall_wash_clothes_unique_id change customer_name_cipher customer_name varchar(88) comment '消费者名称';
alter table mall_wash_collect_info change customer_phone_cipher customer_phone varchar(152) comment '消费者手机';
alter table mall_wash_collect_info change address_cipher address varchar(280) comment '详细地址';
alter table mall_wash_collect_info change customer_name_cipher customer_name varchar(152) comment '消费者姓名';
alter table mall_wash_order_back_address change address_cipher address varchar(280) comment '详细地址';
alter table mall_wash_order_back_address change back_name_cipher back_name varchar(152) comment '还衣收货人';
alter table mall_wash_order_back_address change back_phone_cipher back_phone varchar(152) comment '还衣收货电话';
alter table mall_wash_order_info change customer_name_cipher customer_name varchar(152) comment '消费者姓名';
alter table mall_wash_order_info change customer_phone_cipher customer_phone varchar(88) comment '消费者电话';
alter table mall_wash_order_info change address_cipher address varchar(280) comment '详细地址';
alter table mall_wash_other_back_ways change sender_name_cipher sender_name varchar(152) comment '寄件人姓名';
alter table mall_wash_other_back_ways change sender_phone_cipher sender_phone varchar(152) comment '寄件人电话';
alter table mall_wash_other_back_ways change sender_address_cipher sender_address varchar(812) comment '寄件地址';
