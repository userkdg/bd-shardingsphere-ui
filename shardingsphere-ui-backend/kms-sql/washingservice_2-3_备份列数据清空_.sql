update mall_wash_appointment_order set address_plain = null where 1=1;
update mall_wash_appointment_order set customer_name_plain = null where 1=1;
update mall_wash_appointment_order set customer_phone_plain = null where 1=1;
update mall_wash_back_order set customer_name_plain = null where 1=1;
update mall_wash_back_order set address_plain = null where 1=1;
update mall_wash_back_order set customer_phone_plain = null where 1=1;
update mall_wash_back_order_log set receive_name_plain = null where 1=1;
update mall_wash_back_order_log set phone_plain = null where 1=1;
update mall_wash_back_order_operation_log set old_address_plain = null where 1=1;
update mall_wash_carriage set finished_name_plain = null where 1=1;
update mall_wash_carriage_address set address_plain = null where 1=1;
update mall_wash_clothes_unique_id set customer_phone_plain = null where 1=1;
update mall_wash_clothes_unique_id set customer_name_plain = null where 1=1;
update mall_wash_collect_info set customer_phone_plain = null where 1=1;
update mall_wash_collect_info set address_plain = null where 1=1;
update mall_wash_collect_info set customer_name_plain = null where 1=1;
update mall_wash_order_back_address set address_plain = null where 1=1;
update mall_wash_order_back_address set back_name_plain = null where 1=1;
update mall_wash_order_back_address set back_phone_plain = null where 1=1;
update mall_wash_order_info set customer_name_plain = null where 1=1;
update mall_wash_order_info set customer_phone_plain = null where 1=1;
update mall_wash_order_info set address_plain = null where 1=1;
update mall_wash_other_back_ways set sender_name_plain = null where 1=1;
update mall_wash_other_back_ways set sender_phone_plain = null where 1=1;
update mall_wash_other_back_ways set sender_address_plain = null where 1=1;
