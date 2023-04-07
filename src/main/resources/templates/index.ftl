<#import 'inc/layout.ftl' as Layout>
<@Layout.layout "我的收藏">
    <div id="app" class="row justify-content-md-center">
        <#--侧边日期-->
        <div class="col col-3">
            <div class="flex-shrink-0 p-3 bg-white" style="width: 280px;">
                <ul class="list-unstyled ps-0">
                    <#list datelines as dateline>
                        <li class="mb-1">

                            <button class="dateline btn btn-toggle align-items-center rounded collapsed"
                                    data-bs-toggle="collapse"
                                    data-bs-target="#collapse-${dateline.title}" aria-expanded="true">
                                ${dateline.title}
                            </button>

                            <div class="collapse show" id="collapse-${dateline.title}">
                                <ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
                                    <#list dateline.children as child>
                                        <li><a href="javascript:hendleDateline('${child.title}')" class="link-dark rounded">${child.title}</a></li>
                                    </#list>
                                </ul>
                            </div>

                        </li>
                    </#list>
                </ul>
            </div>
        </div>
        <!---->
        <div class="col col-9" id="collects-col">
            <#include "/inc/collect-tpl.ftl">
            <div class="row" id="masonry"></div>
        </div>
    </div>

    <script>

        var userId = '${userId}'
        if (userId == null || userId == '') {
            userId = '${current.id}'
        }

        var laytpl, flow
        // 初始化layui的模板和瀑布流模块
        layui.use(['laytpl', 'flow'], function () {
            laytpl = layui.laytpl;
            flow = layui.flow;
        });

        // layui的瀑布流加载数据
        function flowLoad(dateline) {
            flow.load({
                elem: '#masonry'
                , isAuto: false
                , end: '哥，这回真的没了~'
                , done: function (page, next) {

                    $.get('/api/collects/' + userId + '/'+ dateline, {
                        page: page,
                        size: 10
                    }, function (res) {
                        var lis = [];

                        var gettpl = $('#collect-card-tpl').html();
                        laytpl(gettpl).render(res.data, function (html) {
                            $(".layui-flow-more").before(html);
                        });

                        next(lis.join(''), page < res.data.totalPages);
                    })

                }
            });
        }
        // 点击时间筛选，重新刷新瀑布流数据
        function hendleDateline(dateline) {
            $('#masonry').html('');
            flowLoad(dateline)
        }
        // 删除操作
        function handleDel(id) {
            layer.confirm('是否确认删除?', function (index) {
                $.post('/api/collect/delete?id=' + id, function (res) {
                    if (res.code == 0) {
                        $('#masonry-item-' + id).remove()
                    }
                    layer.msg(res.mess)
                })
                layer.close(index);
            });
        }
        $(function () {
            // 初始化加载，all表示全部
            flowLoad('all')
        });
    </script>

</@Layout.layout>