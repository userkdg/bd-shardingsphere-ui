alter table mall_wash_appointment_order drop column address_plain;
alter table mall_wash_appointment_order drop column customer_name_plain;
alter table mall_wash_appointment_order drop column customer_phone_plain;
alter table mall_wash_back_order drop column customer_name_plain;
alter table mall_wash_back_order drop column address_plain;
alter table mall_wash_back_order drop column customer_phone_plain;
alter table mall_wash_back_order_log drop column receive_name_plain;
alter table mall_wash_back_order_log drop column phone_plain;
alter table mall_wash_back_order_operation_log drop column old_address_plain;
alter table mall_wash_carriage drop column finished_name_plain;
alter table mall_wash_carriage_address drop column address_plain;
alter table mall_wash_clothes_unique_id drop column customer_phone_plain;
alter table mall_wash_clothes_unique_id drop column customer_name_plain;
alter table mall_wash_collect_info drop column customer_phone_plain;
alter table mall_wash_collect_info drop column address_plain;
alter table mall_wash_collect_info drop column customer_name_plain;
alter table mall_wash_order_back_address drop column address_plain;
alter table mall_wash_order_back_address drop column back_name_plain;
alter table mall_wash_order_back_address drop column back_phone_plain;
alter table mall_wash_order_info drop column customer_name_plain;
alter table mall_wash_order_info drop column customer_phone_plain;
alter table mall_wash_order_info drop column address_plain;
alter table mall_wash_other_back_ways drop column sender_name_plain;
alter table mall_wash_other_back_ways drop column sender_phone_plain;
alter table mall_wash_other_back_ways drop column sender_address_plain;
