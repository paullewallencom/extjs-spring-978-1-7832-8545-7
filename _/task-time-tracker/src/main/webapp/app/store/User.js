Ext.define('TTT.store.User', {
    extend: 'Ext.data.Store',
    requires: ['TTT.model.User'],
    model: 'TTT.model.User',
    proxy: {
        type: 'ajax',
        url: 'user/findAll.json',
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});