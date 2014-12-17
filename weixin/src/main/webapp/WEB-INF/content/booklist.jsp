<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
<title>复旦信息办--信息订阅</title>
<link href="css/bootstrap.min.css" rel="stylesheet">
 <link href="css/bootstrap-theme.min.css" rel="stylesheet">
<script src="js/jquery-1.11.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script type="text/javascript">
function setbook(item,obj)
{
	$.post("book.act",{"item":item,"book":obj.checked},function(resp){
			$("#mmc").html(resp.errdesc);
			$("#mm").modal("show");
		if(resp.errcode!=0)
			obj.checked=!obj.checked;
	},"json");
	}
</script>
</head>

<body>
		<div class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" id="mm"  style="margin-top:100px">
<div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title" id="myModalLabel">操作结果</h4>
      </div>
      <div class="modal-body" id="mmc">
        ...
      </div>
      </div>
      </div>
</div>

	<div class="container-fluid">
		<s:include value="/common/wxnav.jsp" />
		
				<div class="alert alert-warning">本功能正在建设中，部分订阅消息无法收到</div>
				<div class=" col-sm-6 col-md-4">
					<div class=" panel panel-primary">
						<div class="panel-heading">故障告警</div>
						<div class="panel-body">
							<label> <input type="checkbox" onclick="setbook('fail_warning',this)" <s:if test="'fail_warning in items">checked="checked"</s:if> />故障告警
							</label><br />
							<label> <input type="checkbox" onclick="setbook('fail_recovery',this)" <s:if test="'fail_recovery in items">checked="checked"</s:if> />故障告警
							</label>
						</div>
					</div>
				</div>

				<div class=" col-sm-6 col-md-4 ">
					<div class=" panel panel-primary">
						<div class="panel-heading">一卡通</div>
						<div class="panel-body">
							<label><input type="checkbox" onclick="setbook('ecard_balance',this)" <s:if test="'ecard_balance' in items">checked="checked"</s:if> />每日余额提醒 </label><br />
							<label><input type="checkbox" onclick="setbook('ecard_low',this)"
								disabled="disabled"   <s:if test="'ecard_low' in items">checked="checked"</s:if> />低余额提醒</label><br /> <label><input
								type="checkbox" onclick="setbook('ecard_consume',this)" disabled="disabled"   <s:if test="'ecard_consume in items">checked="checked"</s:if> />消费入账提醒</label>
						</div>
					</div>
				</div>

				<div class=" col-sm-6 col-md-4 ">
					<div class=" panel panel-primary">
						<div class="panel-heading">办公系统</div>
						<div class="panel-body">
							<label><input type="checkbox" onclick="setbook('oa_todo',this)"
								disabled="disabled"   <s:if test="'oa_todo' in items">checked="checked"</s:if> />待办事宜 </label><br />
						</div>
					</div>
				</div>
			</div>

	

		<s:include value="/common/foot.jsp" />


	
</body>
</html>