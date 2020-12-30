Ext.define('TTT.model.User', {
    extend: 'Ext.data.Model',
    
    fields: [
        { name: 'username', type: 'string' },
        { name: 'firstName', type: 'string' },
        { name: 'lastName', type: 'string' },
        { name: 'fullName', type: 'string', persist:false },
        { name: 'email', type: 'string' },
        { name: 'password', type: 'string' },
        { name: 'adminRole', type: 'string' }
    ],
    idProperty: 'username',
    proxy: {
        type: 'ajax',
        idParam:'username',
        api:{
            create: 'user/store.json',
            read: 'user/find.json',
            update: 'user/store.json',
            destroy: 'user/remove.json'
        },
        reader: {
            type: 'json',            
            root: 'data'
        },
        writer: {
            type: 'json',
            allowSingle:false,
            encode:true,
            root:'data',
            writeAllFields: true
        }
    },
    validations: [
        {type: 'presence',  field: 'username'},
        {type: 'length', field: 'username', min: 4},
        {type: 'presence',  field: 'firstName'},
        {type: 'length', field: 'firstName', min: 2},
        {type: 'presence',  field: 'lastName'},
        {type: 'length', field: 'lastName', min: 2},
        {type: 'presence',  field: 'email'},
        {type: 'email',  field: 'email'},
        {type: 'presence',  field: 'password'},
        {type: 'length', field: 'password', min: 6},
        {type: 'inclusion', field: 'adminRole', list:['Y','N']}
    ]        
});
