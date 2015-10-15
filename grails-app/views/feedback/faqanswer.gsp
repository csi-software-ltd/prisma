<g:rawHtml>${question.atext}</g:rawHtml>
<g:if test="${question.file_id}">
<g:if test="${Picture.get(question.file_id)?.mimetype=='image/jpeg'}">
<br/><img width="100%" src="${createLink(action:'showscan',id:question.file_id,params:[code:Tools.generateModeParam(question.file_id)])}" />
</g:if><g:else>&nbsp;<a class="button" style="z-index:1" href="${createLink(action:'showscan', id:question.file_id, params:[code:Tools.generateModeParam(question.file_id)])}" title="Скан документа" target="_blank"><i class="icon-picture"></i></a></g:else>
</g:if>