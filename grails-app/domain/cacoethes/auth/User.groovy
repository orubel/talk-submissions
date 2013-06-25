package cacoethes.auth

import cacoethes.Profile

class User {

    transient springSecurityService

    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    static hasOne = [profile: Profile]
    static hasMany = [oauthIds: OauthId, openIds: OpenId]

    static constraints = {
        username blank: false, unique: true
        password blank: false
        profile nullable: true
    }

    static mapping = {
        table '`user`'
        password column: '`password`'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    } 

    protected void encodePassword() {
        password = springSecurityService.encodePassword(password)
    }
}
