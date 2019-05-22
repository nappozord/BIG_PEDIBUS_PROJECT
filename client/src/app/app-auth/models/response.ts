import {UserInfo} from './user-info';

export interface Response {
  name: string;
  message: string;
  success: boolean;
  users: UserInfo[];
}
