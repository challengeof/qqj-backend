package com.qqj.error;

/**
 * User: xudong
 * Date: 3/4/15
 * Time: 2:21 PM
 */
public enum ErrorCode {
    BadCredential(21401, "用户名或密码错误"),
    CustomerAlreadyExists(23400, "用户已存在"), CustomerDoesNotExist(23401, "用户不存在"),

    TooMuchCodeRetry(10001, "验证码重试次数过多，请重新申请验证码"),
    WrongCode(10002, "验证码错误"),
    RequestNotCorrect(10003, "验证码错误"),

    OutStock(30001, "库存不足"),
    SkuNotAvailable(30002, "商品已下线"),
    ActiveRestaurantNotExists(30003, "缺少有效的餐馆"),
    InvalidCoupon(30004, "无效的无忧券"),
    InvalidRefundNo(30005,"退货数量大于销售数量"),
    InvalidSyncRefundEdb(30006, "同步退货到E店宝失败"),


    RestaurantMoreThanOne(21402, "您已经注册过餐馆了" ),
    UserDefined(40004,""),
    CustomerAreaOutside(23403,"对不起，您的收货地址，我们暂未开通配送服务"),
    ScoreExchangeException(30007,"积分兑换异常"),
//    ScoreSendNotStockOutException(30008,"积分派送对应订单无收货信息"),
    OrderNotExist(50001, "订单不存在"),
    OrderStatusHasChanged(50002, "订单状态已改变"),
    OrderLimit(30008, ""),
    ExceedLimited(30009, "赠品已送完!"),
    PurchaseQuantityExcess(40001,""),
    ScoreNotEnough(30015, "积分不足"),
    RestaurantTypeExists(50003,"同一级别的餐馆类型已经存在!"),
    SpikeInvalid(30010, "秒杀已失效"),
    SpikeEnd(30011, "秒杀已结束"),
    SpikeSkuNotEnough(30012, "秒杀商品不足"),
    SpikeCanNotModify(30013, "秒杀商品无法修改，活动即将开始或已开始"),

    CustomerAuditExists(60001, "已存在的审核申请")
    ;



    private int error;

    private String errorMessage;

    private ErrorCode(int error, String message) {
        this.error = error;
        this.errorMessage = message;
    }

    public int getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
