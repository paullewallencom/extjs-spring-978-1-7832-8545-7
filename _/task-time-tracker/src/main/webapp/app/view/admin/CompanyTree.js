Ext.define('TTT.view.admin.CompanyTree', {
    extend: 'Ext.tree.Panel',
    xtype: 'companytree',
    title: 'Company -> Projects -> Tasks',
    requires: ['TTT.store.CompanyTree','Ext.tree.plugin.TreeViewDragDrop'],
    store: 'CompanyTree',
    lines: true,
    rootVisible: false,
    hideHeaders: true,
    viewConfig: {
        preserveScrollOnRefresh: true,
        plugins: {
            ptype: 'treeviewdragdrop'
        }
    },
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            
            tools: [{
                type: 'expand',
                qtip: 'Expand All'
            }, {
                type: 'collapse',
                qtip: 'Collapse All'
            }, {
                type: 'refresh',
                qtip: 'Refresh Tree'
            }],
            columns: [{
                xtype: 'treecolumn',
                dataIndex: 'text',
                flex: 1
            }/*,
        {
            dataIndex:'id',
            width:50
        }*/]
        });
        me.callParent(arguments);
    }
});