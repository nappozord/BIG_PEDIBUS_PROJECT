// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,

  API_BASE_URL: /*'http://nappozord.tk:8080'*/ 'http://localhost:8080',
  ACCESS_TOKEN: 'accessToken',

  OAUTH2_REDIRECT_URI: /*'http://nappozord.tk:8080/oauth2/redirect'*/ 'http://localhost:8080/oauth2/redirect',
  GOOGLE_AUTH_URL: /*'http://nappozord.tk:8080/oauth2/authorize/google?redirect_uri=http://nappozord.tk/oauth2/redirect'*/
    'http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost/oauth2/redirect',
  FACEBOOK_AUTH_URL: 'http://nappozord.tk:8080/oauth2/authorize/facebook?redirect_uri=http://nappozord.tk/oauth2/redirect',

  NAME_MIN_LENGTH: 4,
  NAME_MAX_LENGTH: 40,

  USERNAME_MIN_LENGTH: 3,
  USERNAME_MAX_LENGTH: 15,

  EMAIL_MAX_LENGTH: 40,

  PASSWORD_MIN_LENGTH: 6,
  PASSWORD_MAX_LENGTH: 20
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
