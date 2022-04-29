alter table ec_oms_address_modify_record add receiver_phone_cipher varchar(152) comment '收货人固定电话' after receiver_phone;
alter table ec_oms_exc_reissue_order add receiver_phone_cipher varchar(152) comment '补发收货人固定电话' after receiver_phone;
alter table ec_oms_invoice add buyer_phone_cipher varchar(152) comment '购买方固话' after buyer_phone;
alter table ec_oms_order add receiver_phone_cipher varchar(152) comment '收货人固定电话' after receiver_phone;
alter table ec_oms_order_import add receiver_phone_cipher varchar(152) comment '收货人固定电话' after receiver_phone;
alter table ec_oms_plat_tmall_presale_order add receiver_phone_cipher varchar(152) comment '收货人固定电话' after receiver_phone;
