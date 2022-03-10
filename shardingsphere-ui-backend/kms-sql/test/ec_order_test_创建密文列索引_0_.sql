ALTER TABLE ec_order.ec_oms_sms_management
    ADD INDEX idx_phone_number_cipher (phone_number_cipher (24)) USING BTREE;

ALTER TABLE ec_order.ec_oms_sms_management_sub
    ADD INDEX idx_phone_number_cipher (phone_number_cipher (24)) USING BTREE;

ALTER TABLE ec_order.ec_oms_order
    ADD INDEX idx_receiver_mobile_cipher (receiver_mobile_cipher (24)) USING BTREE;

ALTER TABLE ec_order.ec_oms_plat_order_encrypt_data
    ADD INDEX idx_mobile_cipher (receiver_mobile_decrypted_cipher (24)) USING BTREE;