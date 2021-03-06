﻿<label for="agentperiod_datestart">Дата начала периода:</label>
<g:datepicker class="normal nopad" name="agentperiod_datestart" value="${String.format('%td.%<tm.%<tY',agentperiod.datestart)}" disabled="true"/>
<label for="agentperiod_dateend">Дата окончания периода:</label>
<g:datepicker class="normal nopad" name="agentperiod_dateend" value="${String.format('%td.%<tm.%<tY',agentperiod.dateend)}" min="${String.format('%td.%<tm.%<tY',agentperiod.datestart+1)}" disabled="${agentperiod.is_last?'false':'true'}"/>
<label for="agentperiod_payterm">Условие расчета:</label>
<g:select id="agentperiod_payterm" name="payterm" value="${agentperiod.payterm}" from="['по дням','по месяцам']" keys="01"/>
<label for="agentperiod_calcperiod">Период расчета:</label>
<g:select id="agentperiod_calcperiod" name="calcperiod" value="${agentperiod.calcperiod}" from="['за месяц вперед','за прошлый месяц','за три месяца вперед','за текущий месяц']" keys="0123"/>
<label for="agentperiod_calcrate">Общий процент по периоду:</label>
<input type="text" class="mini" id="agentperiod_calcrate" name="calcrate" value="${number(value:agentperiod.calcrate)}" ${!agentperiod.is_last?'disabled':''}/>
<label for="agentperiod_calccost">Себестоимость по периоду:</label>
<input type="text" class="mini" id="agentperiod_calccost" name="calccost" value="${number(value:agentperiod.calccost)}" ${!agentperiod.is_last?'disabled':''}/>
<label class="auto" for="agentperiod_vrate">Курс:</label>
<input type="text" class="mini" id="agentperiod_vrate" name="vrate" value="${number(value:agentperiod.vrate)}" ${!agentperiod.is_last?'disabled':''}/>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#periodUpdateForm').slideUp();"/>
</div>
<input type="hidden" name="agentagr_id" value="${agentagr.id}"/>
<input type="hidden" name="id" value="${agentperiod.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />