import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor() {
  }
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const jwtToken = localStorage.getItem('token');
    if (jwtToken != null) {
      const cloned = request.clone({
        headers: request.headers.set('Authorization', 'Bearer ' + jwtToken)
      });
      console.log('AuthInterceptor accessToken found: ' + JSON.stringify(jwtToken));
      return next.handle(cloned);
    } else {
      console.log('AuthInterceptor accessToken not found');
      return next.handle(request);
    }
  }
}

