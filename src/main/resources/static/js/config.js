(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        define([], factory);
    } else if (typeof module === 'object' && module.exports) {
        module.exports = factory();
    } else {
        root.OktaConfig = factory();
  }
}(this, function () {
    return {
      orgUrl: 'https://dev-904187.oktapreview.com',
      clientId: '0oadve4kd8K5o0r3x0h7',
      issuer: 'https://dev-904187.oktapreview.com/oauth2/default',
      scopes: ['openid', 'email', 'profile']
    };

}));
