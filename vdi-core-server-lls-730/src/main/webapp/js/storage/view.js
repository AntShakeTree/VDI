/**
 * Created by Administrator on 14-7-29.
 */
//var root = {
//    "name": "Storage",
//    "children": [
//        {
//            "name": "Local Storage",
//            "children": [
//                {
//                    "name": "Local1",
//                    "children": [
//                        {
//                            "name": "vm1"
//                        },
//                        {
//                            "name": "vm2"
//                        },
//                        {
//                            "name": "vm3"
//                        }
//                    ]
//                },
//                {
//                    "name": "Local2",
//                    "children": [
//                        {
//                            "name": "vm4"
//                        },
//                        {
//                            "name": "vm5"
//                        },
//                        {
//                            "name": "vm6"
//                        }
//                    ]
//                },
//                {
//                    "name": "Local3",
//                    "children": [
//                        {
//                            "name": "vm7"
//                        },
//                        {
//                            "name": "vm8"
//                        },
//                        {
//                            "name": "vm9"
//                        }
//                    ]
//                }
//            ]
//        },
//        {
//            "name": "Share Storage",
//            "children": [
//                {
//                    "name": "Share1",
//                    "children": [
//                        {
//                            "name": "vm7"
//                        },
//                        {
//                            "name": "vm8"
//                        },
//                        {
//                            "name": "vm9"
//                        }
//                    ]
//                },
//                {
//                    "name": "Share2",
//                    "children": [
//                        {
//                            "name": "vm7"
//                        },
//                        {
//                            "name": "vm8"
//                        },
//                        {
//                            "name": "vm9"
//                        }
//                    ]
//                },
//                {
//                    "name": "Share3",
//                    "children": [
//                        {
//                            "name": "vm7"
//                        },
//                        {
//                            "name": "vm8"
//                        },
//                        {
//                            "name": "vm9"
//                        }
//                    ]
//                }
//            ]
//        }
//    ]
//};
//
//var m = [20, 120, 20, 120],
//    w = 1280 - m[1] - m[3],
//    h = 800 - m[0] - m[2],
//    i = 0;
//
//var tree = d3.layout.tree()
//    .size([h, w]);
//
//var diagonal = d3.svg.diagonal()
//    .projection(function(d) { return [d.y, d.x]; });
//
//var vis = d3.select("#store_content").append("svg:svg")
//    .attr("width", w + m[1] + m[3])
//    .attr("height", h + m[0] + m[2])
//    .append("svg:g")
//    .attr("transform", "translate(" + m[3] + "," + m[0] + ")");
//
//root.x0 = h / 2;
//root.y0 = 0;
//
//function toggleAll(d) {
//    if (d.children) {
//        d.children.forEach(toggleAll);
//        toggle(d);
//    }
//}
//
//// Initialize the display to show a few nodes.
//root.children.forEach(toggleAll);
//toggle(root.children[1]);
//toggle(root.children[1].children[0]);
//
//update(root);
//
//function update(source) {
//    var duration = d3.event && d3.event.altKey ? 5000 : 500;
//
//    // Compute the new tree layout.
//    var nodes = tree.nodes(root).reverse();
//
//    // Normalize for fixed-depth.
//    nodes.forEach(function(d) { d.y = d.depth * 180; });
//
//    // Update the nodes…
//    var node = vis.selectAll("g.node")
//        .data(nodes, function(d) { return d.id || (d.id = ++i); });
//
//    // Enter any new nodes at the parent's previous position.
//    var nodeEnter = node.enter().append("svg:g")
//        .attr("class", "node")
//        .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
//        .on("click", function(d) { toggle(d); update(d); });
//
//    nodeEnter.append("svg:circle")
//        .attr("r", 1e-6)
//        .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });
//
//    nodeEnter.append("svg:text")
//        .attr("x", function(d) { return d.children || d._children ? -10 : 10; })
//        .attr("dy", ".35em")
//        .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
//        .text(function(d) { return d.name; })
//        .style("fill-opacity", 1e-6);
//
//    // Transition nodes to their new position.
//    var nodeUpdate = node.transition()
//        .duration(duration)
//        .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });
//
//    nodeUpdate.select("circle")
//        .attr("r", 4.5)
//        .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });
//
//    nodeUpdate.select("text")
//        .style("fill-opacity", 1);
//
//    // Transition exiting nodes to the parent's new position.
//    var nodeExit = node.exit().transition()
//        .duration(duration)
//        .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
//        .remove();
//
//    nodeExit.select("circle")
//        .attr("r", 1e-6);
//
//    nodeExit.select("text")
//        .style("fill-opacity", 1e-6);
//
//    // Update the links…
//    var link = vis.selectAll("path.link")
//        .data(tree.links(nodes), function(d) { return d.target.id; });
//
//    // Enter any new links at the parent's previous position.
//    link.enter().insert("svg:path", "g")
//        .attr("class", "link")
//        .attr("d", function(d) {
//            var o = {x: source.x0, y: source.y0};
//            return diagonal({source: o, target: o});
//        })
//        .transition()
//        .duration(duration)
//        .attr("d", diagonal);
//
//    // Transition links to their new position.
//    link.transition()
//        .duration(duration)
//        .attr("d", diagonal);
//
//    // Transition exiting nodes to the parent's new position.
//    link.exit().transition()
//        .duration(duration)
//        .attr("d", function(d) {
//            var o = {x: source.x, y: source.y};
//            return diagonal({source: o, target: o});
//        })
//        .remove();
//
//    // Stash the old positions for transition.
//    nodes.forEach(function(d) {
//        d.x0 = d.x;
//        d.y0 = d.y;
//    });
//}
//
//// Toggle children.
//function toggle(d) {
//    if (d.children) {
//        d._children = d.children;
//        d.children = null;
//    } else {
//        d.children = d._children;
//        d._children = null;
//    }
//}
rootApp.controller('storage.ctrl', function($scope) {
    var add = function() {
        $scope.root.createModal({
            templateUrl : "template/computingpool/create.html",
            size : "",
            width : "600px"
        });
    };
    var edit = function() {
        return null;
    };
    var del = function() {
        return null;
    };
    //数据模型
    $scope.storagemodel = {
        //按钮组
        buttonGroup : [{'id' : 'add', 'cls' : 'button-add', 'val' : 'Create', 'click' : add},
                        {'id' : 'edit', 'cls' : 'button-edit', 'val' : 'Edit', 'click' : edit},
                        {'id' : 'delete', 'cls' : 'button-del', 'val' : 'Delete', 'click' : del}]
    };
    //计算池列表假数据
    $scope.tableData = [{
    	storagename : "sharedstorage",
    	storagetype : "shared",
    	address:"192.168.4.5",
    	status : 1,
    	totalsize : 50,
    	free : 150
    },{
    	storagename : "localstorage",
    	storagetype : "local",
    	address:"192.168.4.91",
    	status : 505,
    	totalsize : 60,
    	free : 140
    }];
    $scope.demo = {
    	    pageNo : 5,
    	    pageSize : 10,
    	    pageAmount : 20
    	};
    $scope.tableOptions = {
            data : 'tableData',
            method : 'POST',
            page : 'tablePage',
            pageNo : 1,
            pageSize : 10,
            sortKey : 'storagename',
            ascend : 1,
            needSelect : true,
            columns :
                [{
                    field : 'storagename',
                    displayName : 'Name',
                    colWidth : ''
                }, {
                    field : 'storagetype',
                    displayName : 'Type',
                    colWidth : ''
                }, {
                    field : 'status',
                    displayName : 'Status',
                    colWidth : '',
                    render:function(v){
                    	var res=null;
                    	switch(v){
                    		case 1:res="正常使用"; break;
                    		case 3:res="主机添加中";break;
                    		case 4:res="主机移除中";break;
                    		case 501:res="创建中";break;
                    		case 502:res="删除中";break;
                    		case 505:res="卸载中";break;
                    		case 506:res="装载中";break;
                    		case 509:res="恢复中";break;
                    	}
                    	return res;
                    }
                }, {
                    field : 'address',
                    displayName : 'Address',
                    colWidth : ''
                }, {
                    field : 'totalsize',
                    displayName : 'Total Size',
                    colWidth : ''
                }, {
                    field : 'free',
                    displayName : 'Available Size',
                    colWidth : ''
                }]
        }
}); 