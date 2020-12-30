Ext.define('TTT.store.Project', {
    extend: 'Ext.data.Store',
    requires: [
        'TTT.model.Project'
    ],
    model: 'TTT.model.Project',
    sorters: [
        {
            property: 'companyName',
            direction: 'ASC'
        }, {
            property: 'projectName',
            direction: 'ASC'
        }
    ],
    proxy: {
        type: 'ajax',
        url:'project/findAll.json',
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});