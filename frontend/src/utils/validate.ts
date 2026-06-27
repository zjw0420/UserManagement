// ====== 通用校验工具 ======

type Rules<T> = Partial<Record<keyof T, Rule>>;

interface Rule {
  label: string;
  required?: boolean;
  pattern?: RegExp;
  patternMsg?: string;
  minLen?: number;
  maxLen?: number;
}

/** 校验表单，返回错误对象，空对象 = 通过 */
export function validate<T extends object>(data: T, rules: Rules<T>): Record<string, string> {
  const errors: Record<string, string> = {};
  for (const key of Object.keys(rules) as (keyof T)[]) {
    const rule = rules[key];
    if (!rule) continue;
    const val = (data as Record<string, unknown>)[key as string];
    const str = typeof val === 'string' ? val.trim() : '';

    // 必填
    if (rule.required && !str) {
      errors[key as string] = rule.label + '不能为空';
      continue;
    }
    if (!str) continue;

    // 正则
    if (rule.pattern && !rule.pattern.test(str)) {
      errors[key as string] = rule.patternMsg || rule.label + '格式不正确';
    }
    // 长度
    if (rule.minLen && str.length < rule.minLen) {
      errors[key as string] = rule.label + `至少${rule.minLen}个字符`;
    }
    if (rule.maxLen && str.length > rule.maxLen) {
      errors[key as string] = rule.label + `最多${rule.maxLen}个字符`;
    }
  }
  return errors;
}

// ====== 预置规则 ======

/** 手机号: 11 位，1 开头 */
export const PHONE_RULE = {
  pattern: /^1[3-9]\d{9}$/,
  patternMsg: '请输入正确的11位手机号',
} as const;

/** 密码: ≥8 位，含大小写字母和数字 */
export const PASSWORD_RULE = {
  pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/,
  patternMsg: '密码需至少8位，包含大小写字母和数字',
} as const;

/** 用户名: 字母数字下划线，2-20 位 */
export const USERNAME_RULE = {
  pattern: /^[a-zA-Z0-9_]{2,20}$/,
  patternMsg: '用户名需2-20位，字母/数字/下划线',
  minLen: 2,
  maxLen: 20,
} as const;

/** 通用必填 */
export const required = (label: string) => ({ label, required: true } as const);
