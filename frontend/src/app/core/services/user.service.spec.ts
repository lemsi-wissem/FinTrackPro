import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { UserService } from './user.service';
import { User } from '../models/user.model';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api/v1/users';

  const mockUser: User = {
    id: 'user-1',
    email: 'alice@example.com',
    firstName: 'Bob',
    lastName: 'Jones',
    role: 'ROLE_USER',
    verified: true,
    createdAt: '2026-01-01T00:00:00Z',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService],
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('updateProfile', () => {
    it('should PUT /users/me with firstName and lastName', () => {
      const request = { firstName: 'Bob', lastName: 'Jones' };
      let result: User | undefined;

      service.updateProfile(request).subscribe((user) => {
        result = user;
      });

      const req = httpMock.expectOne(`${BASE_URL}/me`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(request);
      req.flush(mockUser);

      expect(result).toEqual(mockUser);
    });

    it('should return updated user with new name', () => {
      const request = { firstName: 'Carol', lastName: 'White' };
      let result: User | undefined;

      service.updateProfile(request).subscribe((user) => {
        result = user;
      });

      const req = httpMock.expectOne(`${BASE_URL}/me`);
      req.flush({ ...mockUser, firstName: 'Carol', lastName: 'White' });

      expect(result!.firstName).toBe('Carol');
      expect(result!.lastName).toBe('White');
    });
  });

  describe('changePassword', () => {
    it('should PUT /users/me/password with currentPassword and newPassword', () => {
      const request = { currentPassword: 'old123', newPassword: 'new456789' };
      let completed = false;

      service.changePassword(request).subscribe(() => {
        completed = true;
      });

      const req = httpMock.expectOne(`${BASE_URL}/me/password`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(request);
      req.flush(null, { status: 204, statusText: 'No Content' });

      expect(completed).toBeTrue();
    });

    it('should handle 204 No Content response', () => {
      service.changePassword({ currentPassword: 'old', newPassword: 'newpassword' }).subscribe({
        next: () => {},
        error: () => fail('should not error on 204'),
      });

      const req = httpMock.expectOne(`${BASE_URL}/me/password`);
      req.flush(null, { status: 204, statusText: 'No Content' });
    });
  });
});
