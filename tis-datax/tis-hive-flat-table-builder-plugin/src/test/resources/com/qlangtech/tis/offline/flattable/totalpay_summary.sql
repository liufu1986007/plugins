SELECT
  tp.totalpay_id
, tp.outfee
, regexp_replace(tp.curr_date,'-','') as curr_date
, tp.source_amount
, tp.discount_amount
, tp.coupon_discount
, tp.result_amount
, tp.recieve_amount
, tp.ratio
, tp.status
, tp.entity_id
, tp.is_valid
, from_unixtime(int(tp.op_time), 'yyyyMMddHHmmss') as op_time
, tp.last_ver
, tp.op_user_id
, tp.discount_plan_id
, tp.operator
, from_unixtime(int(tp.operate_date), 'yyyyMMddHHmmss') as operate_date
, tp.card_id
, tp.card
, tp.card_entity_id
, tp.is_full_ratio
, tp.is_minconsume_ratio
, tp.is_servicefee_ratio
, tp.invoice_code
, tp.invoice_memo
, tp.invoice
, tp.over_status
, tp.is_hide
, from_unixtime(int(tp.load_time + '000'), 'yyyyMMddHHmmss') as load_time
, from_unixtime(int(tp.modify_time + '000'), 'yyyyMMddHHmmss') as modify_time
, o.order_id
, o.seat_id
, o.area_id
, o.is_valido
, o.people_count
, o.order_from
, o.order_kind
, o.inner_code
, o.open_time
, o.end_time
, o.order_status
, o.code
, o.seat_code
, o.customer_ids
, o.has_fetch
, o.customerregister_id
, o.mobile order_mobile
, o.address order_address
, o.courier_phone
, o.out_id
, p.kindpay
, p.fee all_pay_fee
, p.pay_customer_ids
, sp.special_fee_summary
, cc.code card_code
, cc.inner_code card_inner_code
, cc.customer_id card_customer_id
, cc.name card_customer_name
, cc.spell card_customer_spell
, cc.mobile card_customer_moble
, cc.phone card_customer_phone
, o.bill_info_final_amount
, sbill.final_amount service_bill_final_amount
, cd_exp.expense_status
, cd_exp.create_time expense_create_time
, (CASE WHEN ((cc.is_enterprise_card > 0) OR (p.is_enterprise_card_pay > 0)) THEN 1 ELSE 0 END) is_enterprise_card
, m.mall_entity_id mall_id
, tp.pt,abs(pmod( hash( cast( tp.entity_id as string) ) , 1)) AS pmod
FROM
  order.totalpayinfo tp
LEFT JOIN tis.order_instance o ON ((tp.totalpay_id = o.totalpay_id) AND o.pt='20200603145304' AND tp.pt='20200603145304')
LEFT JOIN tis.tmp_pay p ON ((tp.totalpay_id = p.totalpay_id) AND p.pt='20200603145304')
LEFT JOIN tis.tmp_group_specialfee sp ON (((tp.totalpay_id = sp.totalpay_id) AND (tp.entity_id = sp.entity_id)) AND sp.pt='20200603145304')
LEFT JOIN tis.tmp_customer_card cc ON (((tp.card_id = cc.id) AND (tp.entity_id = cc.entity_id)) AND cc.pt='20200603145304')
LEFT JOIN order.servicebillinfo sbill ON (((tp.totalpay_id = sbill.servicebill_id) AND (sbill.is_valid = 1)) AND sbill.pt='20200603145304')
LEFT JOIN tis.card_expense_relative cd_exp ON ((tp.totalpay_id = cd_exp.totalpay_id) AND cd_exp.pt='20200603145304')
LEFT JOIN shop.mall_shop m ON (((tp.entity_id = m.shop_entity_id) AND (m.is_valid = 1)) AND m.pt='20200603145304')
WHERE (CAST(tp.curr_date AS bigint) > 20191101)