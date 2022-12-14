MIS

#ВАЖНО!
Если вы написали новый функционал, который отличается от описанного, ОБЯЗАТЕЛЬНО дополните описание.
Если вы считаете, что мой текст нужно изменить или я где то ошибся, то внесите свои коррективы, НО
желательно предварительно обсудить это решение с командой.

#Проект
Медицинская информационная система.
В базу данных должны попадать пациенты.
Должна быть организована талонная система контроля очереди.
Медицинский персонал должен оказывает услуги в соответствии с прайс листом.
Должен формироваться счет из оказанных услуг, который будет отправляться в минздрав для оплаты

#РАЗДЕЛ  -  Описание
репозитории находятся в директории repository
интерфейсы сервисов и их имплементации располагаются в директории service
модели, дто, enums располагаются в директории models
контроллеры располагаются в директории controllers
настройки находятся в директории configs
утилиты находятся в директории utils
На данный момент, проект работает только локально.
Весь функционал контроллеров должен быть покрыт интеграционными тестами.

#Стек
SpringBoot, Hibernate, Rest, JpaRepositories, HQL, PostgreSQL, FlyWay, Junit4, Swagger, Git, Docker

#РАЗДЕЛ  -  Как запустить?
Используйте версию Java - 17.
Установите PostgreSQL, либо воспользуйтесь докером (yml файл есть в директории df в корне проекта).
Клонируете проект к себе локально, подключаете к проекту БД PostgreSQL.
Проходите в ресурсы, обращаем внимание на переменные в пропертях особенно в скобках {SOME}.
Справа вверху возле "молоточка" есть конфигурации загрузки, нажимаем "Edit Configurations".
Тут по необходимости можно создать/изменить конфигурацию того, как будем запускать приложение.
Если прям посередине экрана возле надписи Active Profiles вы не видите Environment variables, то
чуть выше справа есть кнопочка Modify options, в выпадающем меню находим и ставим галочку рядом с Environment variables.
Ищем справа в окне поле "Environment variables", нажимаем в конце этого поля на значок edit.
Используя "+" добавляем все переменные с которыми будет стартовать приложение из числа тех, на которые обращали внимание в пропертях.
Для наполнения БД тестовыми данными из DataInitializer надо выставить в проперти app.runInitialize=true.
Сохраняем.
Запускаем.
Рекомендую выставить app.runInitialize=false, xто бы не инициализировать базу повторно.
Обращаю внимание, к проекту подключен FlyWay и все изменения схемы БД происходят исключительно через написание скрипта.
Для запуска интеграционных тестов используется свой конфиг с другой схемой.
Попробуйте один раз запустить любой тест.
Он должен упасть.
Заходим в директорию test -> resourсes -> properties, обращаем внимание на переменные.
Справа вверху возле "молоточка" нажимаем "Edit Configurations".
Выбираем JUnit, опускаем глаза вертикально вниз до надписи "Edit configuration templates...", нажимаем.
Справа ищем поле "Environment variables".
Заходим в него и прописываем переменные.
Сохраняем.
Тесты должны проходить после этого.
Возможна проблема связанная с FlyWay - необходимо обновить схему для тестов и удалить все таблицы из схемы руками.
#РАЗДЕЛ  -  Как работать?
Для наполнения БД тестовыми данными используется класс DataInitializer.
Работать с приложением можно через браузер, тестировать эндпоинты(методы) REST контроллеров
можно через Postman, можно через Swagger.
Мы - бэкенд разработчики, поэтому я выбрал максимально простой способ разработки фронта.
Страницы с JS загружаются через WebConfig, данные на страницы попадают через REST контроллераы.
Внешний вид страниц (фронт) остается на ваше усмотрение, рекомендую минимализм.
Меня интересует только возможность отображения данных из БД и отработка заявленного функционала.
Вы нашли ошибку в коде, но она не относится к вашей задаче? Ее не надо править.
Если:
вы не уверены, но вам кажется,
ошибка незначительная и возможно выстрелит, но пока не стреляет,
в этом месте код работает неэффективно и надо сделать рефактор,
функционал надо удалить,
и т.д...
то над этим местом в коде ставится "//todo и текст описания ошибки".
Мы это будем называть "техническим долгом" и выполнять когда нет крупной разработки.
Если ошибка грубая и явная, то ставится "//todo и текст описания ошибки",
в Gitlab -> Issues -> Boards вами создается новая карточка и на нее вешается лейбл "Bug".
Карточки в меткой Bug всегда забираются теми, кто закончил текущие задачи и готов
быстро заняться критической ошибкой.
Для этого надо заасигниться на карточку и перетащить в "Doing" и заниматься ее устранением.
На фронт отправляем Dto. У каждой Дто должен быть свой сервис и репозиторий если для получения Dto необходимо делать запросы в БД.
Если ссылка есть, используем MapStruct.
На фронт отправляем Response.
В контроллере принимаем Dto, которая валидирует поля на своем уровне.
В контроллере валидацию бизнес логики осуществляем используя утилитарный класс ApiValidationUtils.
Контроллеры и эндпоинты должны быть описаны Swagger-ом.
Запросы к БД предпочтительно писать на HQL.
Если такой возможности нет, используем SQL.
Простые Dto можно получать через конструктор в запросе, для получения сложных можно делать несколько запросов вызываемых из сервиса.
Так же можно использовать Tuple.
Рекомендую делать maven -> clean после любых операций с git
Я ожидаю, что вы перед выполнением карточки запускаете интеграционные тесты, проверяя, что все тесты проходят.
Выполняете свою задачу, покрываете написанный вами функционал тестами.
Если ваш код явно вносит изменения в выполнение други тестов и вы !! понимаете !!, почему произходит та или иная правка и она не ломает сути теста, правите тесты которые сломались.
Если перед выполнением вашей карточки все тесты работали, вы написали код и тесты начали падать, но вы их не трогали, разбирайтесь, вероятнее всего вы их сломали.
Самое распространенное - не почистили таблицы после своего теста.
Перед отправкой мердж реквеста ВСЕГДА! мерджите последние изменения main ветки в свою, резолвите конфликты если такие есть, выполняете maven -> clean, запускаете интеграционные тесты. Они должны все пройти. Отправка неработающего функционала запрещена.
При решении конфликтов настоятельно рекомендую не доверять гиту автоматически их решать.
Откроется окно в котором будут конфликты в строку, двойным кликом открываем каждый конфликт.
Откроется окно из трех окон. С одной стороны код из вашей ветки, с другой из main, а посередине - результат.
Ваша задача привнести свой функционал не сломав функционал уже работающий в main.
В тестах обратите внимание, есть данные с которыми запускается тест и данные, которые тест удаляет после своей работы.
ВСЕГДА чистите БД после своего теста, иначе будут падать следующие тесты.
Для каждого контроллера создается своя директория со своими датасетами.
#Частые ошибки
-Нейминг.
Если вы создали ссылку, то по ее имени должно быть понятно, на что она указывает.
Если вы создали метод, по его имени должно быть понятно, что он делает.
-Ненужные ссылки.
Если у вас есть метод, который должен возвращать объект, не надо создавать ссылку на этот объект,
а следующей строкой возвращать его. Сразу возвращай объект.
-getSingleResult().
Есть запросы, в которых вы явно можете получить только один объект. Не надо там получать список объектов,
потом стримами обрабатывать его, что бы получить первый результат. Сразу получайте один объект в запросе.
-Неиспользуемый функционал.
Написание лишнего функционала некорректно. Вы тратите время когда пишете его, когда кто-либо читает его,
зтот функционал надо обслуживать, о нем надо думать. Не пишите функционал 'про запас' - только то, что необходимо сейчас.
-Автосвязывание.
Не рекомендуется связывать бины через поле.
Связывайте через конструктор обязательные поля, через сеттер опциональные.
-id в конструкторе сущности.
Не используйте конструктор с id для сущностей под управлением Hibernate. Назначать id должны не вы в ручную,
а Hibernate с СУБД. Иногда такая необходимость появляется, но если вы его не используете, то удалите id из параметров.
#РАЗДЕЛ  -  WTF Git?
Я расчитываю, что вы откроете Интернет и поищете информацию, поэтому описываю в кратце один из способов.
Открываем Gitlab -> Issues -> Boards
Либо у вас есть карточка, либо вы заасигнитесь на нее самостоятельно.
Брать карточки можно из "To do" листа.
Когда вы приступаете к выполнению карточки, перетаскиваете ее в "Doing"
У вас есть проект, загруженный в Идею.
Вы хотите выполнить карточку, которую взяли в работу.
Справа внизу указана ваша текущая ветка.
Слева внизу есть вкладка Git, раскрываете ее, нажимаете Fetch All Remotes. Ждем окончания загрузки.
После этого, с удаленного сервера подтянутся все актуальные ветки.
Находим Remote -> origin -> main, ПКМ на main и выбираем "New Branch from Selected..."
Вводите "номер карточки"-"тип задачи"-"краткое описание задачи в 2х-3х словах латиницей"
пример: 914-BUG-test-data-init-failed
типы задач:
TODO - технический долг,
BUG - значительные ошибки,
FIX - незначительные ошибки,
REFACTOR - рефакторингу,
FEATURE - новый функционал
Локально будет создана новая ветка от main и сразу выполнен checkout на нее.
Необходимо выполнить clean в Maven и попробовать собрать проект, вдруг он не работает.
Вы пишете код в своей ветке, а в это время удаленная ветка "ушла вперед".
Вы хотите привнести свой функционал, но могут быть конфликты, поэтому всегда исходим из того,
что код на удаленном сервере править нельзя. Вы привносите функционал, поэтому адаптируете свой код.
Проверьте, что справа внизу вы на вашей локальной ветке, в которой вы делаете текущую задачу.
Слева внизу есть вкладка Git, раскрываете ее, нажимаете Fetch All Remotes. Ждем окончания загрузки.
После этого, с удаленного сервера подтянутся все актуальные ветки.
Находим Remote -> origin -> main, ПКМ на main и выбираем "Pull into Current Using Merge"
Актуальное состояние main ветки будет смерджено с вашей локальной веткой.
Возможен конфликт, рекомендую посмотреть в Интеренете информацию по устранению конфликтов.
Когда все выполнено, необходимо выполнить clean в Maven и попробовать собрать проект.
Так же необходимо выполнить все тесты(когда они будут).
Если все хорошо, коммитим изменения, пишем очень краткое описание того, что делает этот коммит.
Пушим коммит(ы)(справа вверху зеленая стрелка "Push...") на удаленный репозиторий.
Открываем Gitlab -> Repository -> Branches
Вам необходимо сделать Merge request - это "заявка" на мердж вашей ветки в main ветку.
Автоматически запускаются папалайны - внутренние проверки проекта(сбилдить, запустить тесты...),
но на данный момент они failed, т.к. еще рано - не пугаемся.
Находим свою ветку и нажимаем "Merge request".
Видим, имя вашей ветки и намерение смерджить ее в main. Иногда бывает необходимо изменить ветку назначения.
в поле Title вставляем имя вашей ветки,а в поле Description можно все подробно описать о чем накипело.
Галочка "Delete source branch when merge request is accepted." говорит о том, что ваша ветка будет удалена
из удаленного репозитория после мерджа функционала в ветку назначения. (галочка должна быть)
Нажимаем синюю кнопку "Create merge request"
Открываем Gitlab -> Issues -> Boards
перетаскиваем вашу карточку в Cross-review
И берем новую задачу.
Товарищи! Помимо писания вашего замечательного кода, надо уметь читать чужой плохой код,
научиться аккуратно делать замечания, высказывать свои мнения, вести дискуссию, слушать замечания в свой адрес,
но и правильно реагировать на это. Поэтому, прежде чем я смерджу карточку из Cross-review,
я хочу что бы ее посмотрели другие студенты.
Для этого открываем Gitlab -> Merge requests
выбираем тот merge request который находится в need review, справа наверху переходим на вкладку "Changes".
Там можно посмотреть, на изменения и написать свои комментарии.
Если функционала написано много и хотелось бы посмотреть целиком, в Идее можно зачекаутиться на эту ветку и
пощупать функционал.
Если/когда вы согласны с написанным функционалом, в GitLab нажимаете кнопку Approve.
#Я ожидаю командной работы, взаимопомощи, терпения и понимания.
#РАЗДЕЛ  -  Интеграция со стронним сервисом
В МИС есть персональные данные пациентов. Есть несколько сценариев, по которым данные могут стать невалидными.
Есть человеческий фактор - медрегистратор ошибся, есть фактор смены документов пациентом, о чем МИС неизвестно.
Для проверки этих данных в реальной жизни используется база застрахованных ТФОМС.
В нашем случае, у нас есть наш сервис, имитирующий сервис ТФОМС.
Используя Rest Template, мы можем отправлять запросы к тестовому сервису и получать ответы от него:

Сервис должен генерировать jwt и возвращать его после авторизации.
Можно узнать статус активности сервиса.
Можно передать персональные данные пациента и получить в ответ правильные данные из БД сервиса.

В перспективе рядом с RestTemplate написать реализацию на gRPC