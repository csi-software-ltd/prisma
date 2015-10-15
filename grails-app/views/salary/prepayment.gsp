<label for="prepayment_pers_name">Фамилия ИО:</label>
<input type="text" id="prepayment_pers_name" disabled value="${pers?.shortname}"/>
<label for="prepayment_prepayment">Сумма аванса:</label>
<input type="text" id="prepayment_prepayment" name="prepayment" value="${intnumber(value:prepayment.prepayment)}"/>
<label for="prepayment_prevfix">Корректировка:</label>
<input type="text" id="prepayment_prevfix" disabled value="${number(value:prepayment.prevfix)}"/>
<div class="clear"></div>
<div class="fright">
  <input type="submit" id="prepaymentupdate_submit_button" class="button" value="Сохранить" />
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#prepaymentupdateForm').slideUp();"/>
</div>
<input type="hidden" name="id" value="${prepayment.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />