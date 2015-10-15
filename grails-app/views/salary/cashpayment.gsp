<label for="prepayment_pers_name">Фамилия ИО:</label>
<input type="text" id="prepayment_pers_name" disabled value="${pers?.shortname}"/>
<g:if test="${!cashpayment.department_id}">
<label for="cashpayment_actsalary">Факт. оклад:</label>
<input type="text" class="auto" id="cashpayment_actsalary" name="actsalary" value="${intnumber(value:cashpayment.actsalary)}"/>
</g:if><g:else>
<label for="cashpayment_cash">К выплате:</label>
<input type="text" class="auto" id="cashpayment_cash" disabled value="${intnumber(value:cashpayment.cash)}"/>
<label for="cashpayment_bonus">Бонус:</label>
<input type="text" class="auto" id="cashpayment_bonus" name="bonus" value="${intnumber(value:cashpayment.bonus)}"/>
<label for="cashpayment_shtraf">Штраф:</label>
<input type="text" class="auto" id="cashpayment_shtraf" name="shtraf" value="${intnumber(value:cashpayment.shtraf)}"/>
<label for="cashpayment_overloadhour">Переработка(дн.):</label>
<input type="text" class="auto" id="cashpayment_overloadhour" name="overloadhour" value="${cashpayment.overloadhour}" onkeyup="computeOverloadSumma(${cashpayment.actsalary})"/>
<label for="cashpayment_overloadsumma">Переработка(руб):</label>
<input type="text" class="auto" id="cashpayment_overloadsumma" name="overloadsumma" value="${intnumber(value:cashpayment.overloadsumma)}"/>
<label for="cashpayment_holiday">Отпускные:</label>
<input type="text" class="auto" id="cashpayment_holiday" name="holiday" value="${intnumber(value:cashpayment.holiday)}"/>
<label for="cashpayment_precashpayment">Оплата до срока:</label>
<input type="text" class="auto" id="cashpayment_precashpayment" name="precashpayment" value="${intnumber(value:cashpayment.precashpayment)}"/>
<label for="cashpayment_prevfix">Корректировка:</label>
<input type="text" class="auto" id="cashpayment_prevfix" name="prevfix" value="${number(value:cashpayment.prevfix)}"/>
</g:else>
<div class="clear"></div>
<div class="fright">
  <input type="submit" id="cashpaymentupdate_submit_button" class="button" value="Сохранить" />
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#cashpaymentupdateForm').slideUp();"/>
</div>
<input type="hidden" name="id" value="${cashpayment.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />