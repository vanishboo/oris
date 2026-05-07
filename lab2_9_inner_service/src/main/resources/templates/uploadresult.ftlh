<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>Главная страница</title>
</head>
<body>

    <h2>Картинки</h2>

    <a href="/showimg">Показать картинки</a>

    <#if avgHttp?? || avgNats??>
        <h3>Сравнение времени отклика</h3>
        <table border="1" cellpadding="6">
            <tr><th>Транспорт</th><th>Запросов</th><th>Среднее, мс</th><th>Все замеры, мс</th></tr>
            <tr>
                <td>HTTP (прямой)</td>
                <td>${(timingsHttp?size)!0}</td>
                <td>${(avgHttp)!0}</td>
                <td>${(timingsHttp?join(", "))!""}</td>
            </tr>
            <tr>
                <td>NATS (брокер)</td>
                <td>${(timingsNats?size)!0}</td>
                <td>${(avgNats)!0}</td>
                <td>${(timingsNats?join(", "))!""}</td>
            </tr>
        </table>
    </#if>

    <#list (imgs)![] as img>
    <div>
        <img src="data:image/jpeg;base64,${img}"></img>
    </div>
    </#list>


    <#list (imgs2)![] as img>
    <div>
        <img src="data:image/jpeg;base64,${img}"></img>
    </div>
    </#list>

</body>
</html>
