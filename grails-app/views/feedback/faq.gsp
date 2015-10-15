<style type="text/css">
  table.list tbody tr td{cursor:pointer;}
  table.list tbody tr.current td{cursor:default;color:#157dfb;font-style:italic;}
  table.list tbody tr.detail td{padding:0;border:none;}
  .detailed{background:#ccc;padding:10px;cursor:default;border-radius:0 0 8px 8px;}
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Вопрос</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${faqlist}" status="i" var="record">
        <tr onclick="getdetail(this,${record.id})">
          <td><g:rawHtml>${record.qtext}</g:rawHtml></td>
        </tr>       
        <tr class="detail"><td><div class="detailed" style="display:none"></div></td></tr>
      </g:each>
      <g:if test="${!faqlist}">
        <tr>
          <td class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Часто задаваемых вопросов не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
